package io.github.shuzhuoi.synology.internal;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyApiException;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Synology WebAPI 的统一执行器。
 * <p>
 * 这里集中处理 API 通用参数、SID 注入、HTTP 调用、JSON 响应解析和 DSM 错误转换，
 * 避免每个业务客户端重复拼接 URL 和判断 success。
 */
public class SynologyApiExecutor {

    private final SynologyDsmConfig config;
    private final SynologyHttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * 会话管理器由 SynologyDsmClient 初始化后注入，用于认证接口自动追加 _sid。
     */
    private SynologySessionManager sessionManager;
    /**
     * 是否在认证请求遇到会话失效错误码时自动重新登录并重试一次。
     * 默认关闭，由 SynologyDsmClient 根据配置开启。
     */
    private boolean autoRefreshSession;

    public SynologyApiExecutor(SynologyDsmConfig config, SynologyHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
        this.objectMapper = SynologyObjectMapper.get();
    }

    public void setSessionManager(SynologySessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setAutoRefreshSession(boolean autoRefreshSession) {
        this.autoRefreshSession = autoRefreshSession;
    }

    /**
     * 判断 DSM 错误码是否表示会话已失效，需要重新登录。
     * <ul>
     *   <li>106: Session timeout</li>
     *   <li>107: Session interrupted by duplicate login</li>
     *   <li>119: SID not found</li>
     * </ul>
     */
    private static boolean isSessionError(Integer code) {
        if (code == null) {
            return false;
        }
        return code == 106 || code == 107 || code == 119;
    }

    public <T> T getPublic(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> dataType) {
        return executeJson(path, apiName, version, method, parameters, dataType, false);
    }

    public <T> Map<String, T> getPublicMap(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> valueType) {
        JsonNode dataNode = executeDataNode(path, apiName, version, method, parameters, false);
        JavaType mapType = objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, valueType);
        return objectMapper.convertValue(dataNode, mapType);
    }

    public <T> T getAuthenticated(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> dataType) {
        try {
            return executeJson(path, apiName, version, method, parameters, dataType, true);
        } catch (SynologyApiException e) {
            // 会话失效时自动重新登录并重试一次，避免长连接场景下因 SID 过期中断业务。
            if (autoRefreshSession && isSessionError(e.getErrorCode())) {
                sessionManager.refresh();
                return executeJson(path, apiName, version, method, parameters, dataType, true);
            }
            throw e;
        }
    }

    public SynologyHttpResponse downloadAuthenticated(String path, String apiName, int version, String method, Map<String, String> parameters) {
        Map<String, String> merged = baseParameters(apiName, version, method, parameters);
        addSid(merged);
        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.GET)
                .url(config.resolveWebApiUrl(path))
                .parameters(merged)
                .connectTimeoutMillis(config.getConnectTimeoutMillis())
                .readTimeoutMillis(config.getReadTimeoutMillis())
                .build();
        SynologyHttpResponse response = httpClient.execute(request);
        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            throw new SynologyHttpException("download request failed with HTTP status " + response.getStatusCode());
        }
        return response;
    }

    public <T> T executeMultipartAuthenticated(String path, String apiName, int version, String method, SynologyHttpRequest multipartRequest, Class<T> dataType) {
        try {
            return doExecuteMultipartAuthenticated(path, apiName, version, method, multipartRequest, dataType);
        } catch (SynologyApiException e) {
            // 上传同样可能在 SID 失效时触发 106/107/119，重试一次避免大文件重新拼装后失败。
            if (autoRefreshSession && isSessionError(e.getErrorCode())) {
                sessionManager.refresh();
                return doExecuteMultipartAuthenticated(path, apiName, version, method, multipartRequest, dataType);
            }
            throw e;
        }
    }

    private <T> T doExecuteMultipartAuthenticated(String path, String apiName, int version, String method, SynologyHttpRequest multipartRequest, Class<T> dataType) {
        Map<String, String> merged = baseParameters(apiName, version, method, multipartRequest.getParameters());
        addSid(merged);
        SynologyHttpRequest.Builder builder = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.POST)
                .url(config.resolveWebApiUrl(path))
                .parameters(merged)
                .connectTimeoutMillis(config.getConnectTimeoutMillis())
                .readTimeoutMillis(config.getReadTimeoutMillis());
        for (int i = 0; i < multipartRequest.getMultipartParts().size(); i++) {
            builder.multipartPart(multipartRequest.getMultipartParts().get(i));
        }
        SynologyHttpResponse response = httpClient.execute(builder.build());
        return parseBody(apiName, method, response.getBody(), dataType);
    }

    private <T> T executeJson(String path, String apiName, int version, String method, Map<String, String> parameters, Class<T> dataType, boolean authenticated) {
        JsonNode dataNode = executeDataNode(path, apiName, version, method, parameters, authenticated);
        if (dataType == Object.class || dataNode == null || dataNode.isNull()) {
            return null;
        }
        return objectMapper.convertValue(dataNode, dataType);
    }

    private JsonNode executeDataNode(String path, String apiName, int version, String method, Map<String, String> parameters, boolean authenticated) {
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
        return parseDataNode(apiName, method, response.getBody());
    }

    private <T> T parseBody(String apiName, String method, String body, Class<T> dataType) {
        JsonNode dataNode = parseDataNode(apiName, method, body);
        if (dataType == Object.class) {
            return null;
        }
        if (dataNode == null || dataNode.isNull()) {
            return newEmptyResponse(apiName, method, dataType);
        }
        return objectMapper.convertValue(dataNode, dataType);
    }

    private JsonNode parseDataNode(String apiName, String method, String body) {
        // DSM 标准 JSON 响应形如 {"success":true,"data":{...}} 或 {"success":false,"error":{...}}。
        if (body == null || body.trim().length() == 0) {
            throw new SynologyHttpException("empty response body from " + apiName + "." + method);
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                JsonNode codeNode = root.path("error").path("code");
                Integer code = codeNode.isMissingNode() ? null : Integer.valueOf(codeNode.asInt());
                throw new SynologyApiException(apiName, method, code, body);
            }
            return root.get("data");
        } catch (IOException e) {
            throw new SynologyDsmException("failed to parse response from " + apiName + "." + method, e);
        }
    }

    private <T> T newEmptyResponse(String apiName, String method, Class<T> dataType) {
        try {
            return dataType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new SynologyDsmException("empty response data from " + apiName + "." + method, e);
        }
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
}
