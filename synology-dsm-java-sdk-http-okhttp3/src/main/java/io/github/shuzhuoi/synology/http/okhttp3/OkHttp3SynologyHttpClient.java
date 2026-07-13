package io.github.shuzhuoi.synology.http.okhttp3;

import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;
import io.github.shuzhuoi.synology.http.okhttp3.internal.response.OkHttpResponseInputStream;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于 OkHttp 3.x 的 Synology HTTP 适配器。
 * <p>
 * 该实现只转换 core 定义的统一 HTTP 对象，不识别具体 Synology 业务 API。
 */
public class OkHttp3SynologyHttpClient implements SynologyHttpClient {

    private static final MediaType BINARY_MEDIA_TYPE = MediaType.parse("application/octet-stream");

    private final OkHttpClient httpClient;

    /**
     * 使用 OkHttp 默认配置创建适配器。
     */
    public OkHttp3SynologyHttpClient() {
        this(new OkHttpClient());
    }

    /**
     * 注入用户配置的 OkHttpClient，便于复用代理、TLS 和连接池等配置。
     *
     * @param httpClient 用户提供的 OkHttpClient
     */
    public OkHttp3SynologyHttpClient(OkHttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("httpClient must not be null");
        }
        this.httpClient = httpClient;
    }

    @Override
    public SynologyHttpResponse execute(SynologyHttpRequest request) {
        try {
            OkHttpClient requestClient = createRequestClient(request);
            Response response = requestClient.newCall(createRequest(request)).execute();
            return convertResponse(request, response);
        } catch (IOException e) {
            throw new SynologyHttpException("failed to execute Synology HTTP request", e);
        } catch (RuntimeException e) {
            throw new SynologyHttpException("failed to execute Synology HTTP request", e);
        }
    }

    private OkHttpClient createRequestClient(SynologyHttpRequest request) {
        // newBuilder 会共享原客户端的连接池和调度器，只覆盖本次 SDK 请求的超时配置。
        return httpClient.newBuilder()
                .connectTimeout(request.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(request.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    private Request createRequest(SynologyHttpRequest request) {
        if (request.getMethod() == SynologyHttpMethod.GET && !request.isMultipart()) {
            return createGetRequest(request);
        }
        return new Request.Builder()
                .url(request.getUrl())
                .post(createPostBody(request))
                .build();
    }

    private Request createGetRequest(SynologyHttpRequest request) {
        HttpUrl url = HttpUrl.parse(request.getUrl());
        if (url == null) {
            throw new IllegalArgumentException("invalid request url: " + request.getUrl());
        }
        HttpUrl.Builder urlBuilder = url.newBuilder();
        for (Map.Entry<String, String> parameter : request.getParameters().entrySet()) {
            urlBuilder.addQueryParameter(parameter.getKey(), parameter.getValue());
        }
        return new Request.Builder().url(urlBuilder.build()).get().build();
    }

    private RequestBody createPostBody(SynologyHttpRequest request) {
        if (request.isMultipart()) {
            return createMultipartBody(request);
        }
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> parameter : request.getParameters().entrySet()) {
            formBuilder.add(parameter.getKey(), parameter.getValue());
        }
        return formBuilder.build();
    }

    private RequestBody createMultipartBody(SynologyHttpRequest request) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, String> parameter : request.getParameters().entrySet()) {
            multipartBuilder.addFormDataPart(parameter.getKey(), parameter.getValue());
        }
        for (SynologyMultipartPart part : request.getMultipartParts()) {
            appendMultipartPart(multipartBuilder, part);
        }
        return multipartBuilder.build();
    }

    private void appendMultipartPart(MultipartBody.Builder builder, SynologyMultipartPart part) {
        Object value = part.getValue();
        if (value instanceof String) {
            builder.addFormDataPart(part.getName(), (String) value);
            return;
        }
        if (value instanceof File) {
            File file = (File) value;
            builder.addFormDataPart(part.getName(), file.getName(), RequestBody.create(BINARY_MEDIA_TYPE, file));
            return;
        }
        throw new IllegalArgumentException("unsupported multipart value type: "
                + (value == null ? "null" : value.getClass().getName()));
    }

    private SynologyHttpResponse convertResponse(SynologyHttpRequest request, Response response) throws IOException {
        if (request.getResponseBodyMode() == ResponseBodyMode.STREAM) {
            return convertStreamResponse(response);
        }
        try {
            ResponseBody responseBody = response.body();
            String body = responseBody == null ? null : responseBody.string();
            return new SynologyHttpResponse(response.code(), response.headers().toMultimap(), body, null);
        } finally {
            response.close();
        }
    }

    private SynologyHttpResponse convertStreamResponse(Response response) {
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            response.close();
            return new SynologyHttpResponse(response.code(), response.headers().toMultimap(), null, null);
        }
        InputStream bodyStream = new OkHttpResponseInputStream(responseBody.byteStream(), response);
        return new SynologyHttpResponse(response.code(), response.headers().toMultimap(), null, bodyStream);
    }
}
