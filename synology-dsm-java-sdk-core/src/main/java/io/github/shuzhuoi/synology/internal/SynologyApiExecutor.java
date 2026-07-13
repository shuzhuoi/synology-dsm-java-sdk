package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyApiException;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;
import io.github.shuzhuoi.synology.internal.request.SynologyApiRequest;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Synology WebAPI 的统一执行器。
 * <p>
 * 这里集中处理 API 通用参数、SID 注入和 HTTP 调用，响应解析交给 SynologyResponseParser，
 * 避免每个业务客户端重复拼接 URL 和判断 DSM 标准响应。
 */
public class SynologyApiExecutor {

    private final SynologyDsmConfig config;
    private final SynologyHttpClient httpClient;
    private final SynologyResponseParser responseParser;

    /**
     * 会话管理器由 SynologyDsmClient 初始化后注入，用于认证接口自动追加 _sid。
     */
    private SynologySessionManager sessionManager;
    /**
     * 会话失效后的刷新重试策略。
     * 默认关闭，由 SynologyDsmClient 根据配置开启。
     */
    private SynologySessionRetryPolicy sessionRetryPolicy = new SynologySessionRetryPolicy(false);

    public SynologyApiExecutor(
            SynologyDsmConfig config,
            SynologyHttpClient httpClient,
            SynologyJsonCodec jsonCodec) {
        this.config = config;
        this.httpClient = httpClient;
        this.responseParser = new SynologyResponseParser(jsonCodec);
    }

    public void setSessionManager(SynologySessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setAutoRefreshSession(boolean autoRefreshSession) {
        this.sessionRetryPolicy = new SynologySessionRetryPolicy(autoRefreshSession);
    }

    /**
     * 设置会话重试策略，主要供后续测试或高级扩展使用。
     */
    public void setSessionRetryPolicy(SynologySessionRetryPolicy sessionRetryPolicy) {
        if (sessionRetryPolicy == null) {
            throw new IllegalArgumentException("sessionRetryPolicy must not be null");
        }
        this.sessionRetryPolicy = sessionRetryPolicy;
    }

    public <T> T getPublic(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> dataType) {
        return executeJson(path, apiName, version, method, parameters, dataType, false);
    }

    public <T> Map<String, T> getPublicMap(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> valueType) {
        String body = executeForBody(path, apiName, version, method, parameters, false);
        return responseParser.parseMap(apiName, method, body, valueType);
    }

    public <T> T getAuthenticated(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> dataType) {
        return executeWithSessionRetry(new AuthenticatedOperation<T>() {
            @Override
            public T execute() {
                return executeJson(path, apiName, version, method, parameters, dataType, true);
            }
        });
    }

    public SynologyHttpResponse downloadAuthenticated(String path, String apiName, int version, String method, Map<String, String> parameters) {
        SynologyApiRequest request = SynologyApiRequest.builder()
                .path(path)
                .apiName(apiName)
                .version(version)
                .method(method)
                .authenticated(true)
                .parameters(parameters)
                .responseType(SynologyHttpResponse.class)
                .responseBodyMode(ResponseBodyMode.STREAM)
                .build();
        return executeAuthenticated(request);
    }

    public <T> T executeMultipartAuthenticated(String path, String apiName, int version, String method, SynologyHttpRequest multipartRequest, Class<T> dataType) {
        SynologyApiRequest.Builder builder = SynologyApiRequest.builder()
                .path(path)
                .apiName(apiName)
                .version(version)
                .method(method)
                .authenticated(true)
                .parameters(multipartRequest.getParameters())
                .responseType(dataType)
                .httpMethod(SynologyHttpMethod.POST);
        for (SynologyMultipartPart part : multipartRequest.getMultipartParts()) {
            builder.multipartPart(part);
        }
        return executeAuthenticated(builder.build());
    }

    /**
     * 执行已封装的 Synology API 请求。
     * <p>
     * 流式响应保持阶段 4 的行为，不解析 JSON 错误体；文本和 multipart 响应参与会话失效重试。
     */
    @SuppressWarnings("unchecked")
    public <T> T executeAuthenticated(final SynologyApiRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (!request.isAuthenticated()) {
            throw new IllegalArgumentException("request must be authenticated");
        }
        if (request.getResponseBodyMode() == ResponseBodyMode.STREAM) {
            return (T) executeStream(request);
        }
        return executeWithSessionRetry(new AuthenticatedOperation<T>() {
            @Override
            public T execute() {
                return executeText(request);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T executeText(SynologyApiRequest request) {
        SynologyHttpResponse response = httpClient.execute(buildHttpRequest(request));
        return (T) responseParser.parseBody(request.getApiName(), request.getMethod(), response.getBody(), request.getResponseType());
    }

    private SynologyHttpResponse executeStream(SynologyApiRequest request) {
        SynologyHttpResponse response = httpClient.execute(buildHttpRequest(request));
        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            throw new SynologyHttpException("download request failed with HTTP status " + response.getStatusCode());
        }
        return response;
    }

    private SynologyHttpRequest buildHttpRequest(SynologyApiRequest request) {
        Map<String, String> merged = baseParameters(request.getApiName(), request.getVersion(), request.getMethod(), request.getParameters());
        if (request.isAuthenticated()) {
            addSid(merged);
        }
        SynologyHttpRequest.Builder builder = SynologyHttpRequest.builder()
                .method(request.getHttpMethod())
                .url(config.resolveWebApiUrl(request.getPath()))
                .parameters(merged)
                .connectTimeoutMillis(config.getConnectTimeoutMillis())
                .readTimeoutMillis(config.getReadTimeoutMillis())
                .responseBodyMode(request.getResponseBodyMode());
        for (SynologyMultipartPart part : request.getMultipartParts()) {
            builder.multipartPart(part);
        }
        return builder.build();
    }

    private <T> T executeJson(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> dataType, boolean authenticated) {
        String body = executeForBody(path, apiName, version, method, parameters, authenticated);
        return responseParser.parseJson(apiName, method, body, dataType);
    }

    private String executeForBody(String path, String apiName, int version, String method, Map<String, String> parameters, boolean authenticated) {
        Map<String, String> merged = baseParameters(apiName, version, method, parameters);
        if (authenticated) {
            addSid(merged);
        }
        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.GET)
                .url(config.resolveWebApiUrl(path))
                .parameters(merged)
                .connectTimeoutMillis(config.getConnectTimeoutMillis())
                .readTimeoutMillis(config.getReadTimeoutMillis())
                .build();
        SynologyHttpResponse response = httpClient.execute(request);
        return response.getBody();
    }

    private Map<String, String> baseParameters(String apiName, int version, String method, Map<String, String> parameters) {
        // 每个 Synology WebAPI 请求都必须携带 api、version、method 三个基础参数。
        Map<String, String> merged = new LinkedHashMap<String, String>();
        merged.put("api", apiName);
        merged.put("version", String.valueOf(version));
        merged.put("method", method);
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (entry.getValue() != null) {
                    merged.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return merged;
    }

    private void addSid(Map<String, String> parameters) {
        // 使用 format=sid 登录时，官方要求后续请求通过 _sid 参数携带会话。
        if (sessionManager == null) {
            throw new SynologyDsmException("sessionManager is not initialized");
        }
        parameters.put("_sid", sessionManager.currentSession().getSid());
    }

    private <T> T executeWithSessionRetry(AuthenticatedOperation<T> operation) {
        try {
            return operation.execute();
        } catch (SynologyApiException e) {
            // 会话失效时自动重新登录并重试一次，避免长连接场景下因 SID 过期中断业务。
            if (sessionRetryPolicy.shouldRefreshAndRetry(e, 0)) {
                refreshSession();
                return operation.execute();
            }
            throw e;
        }
    }

    private void refreshSession() {
        if (sessionManager == null) {
            throw new SynologyDsmException("sessionManager is not initialized");
        }
        sessionManager.refresh();
    }

    private interface AuthenticatedOperation<T> {
        T execute();
    }
}
