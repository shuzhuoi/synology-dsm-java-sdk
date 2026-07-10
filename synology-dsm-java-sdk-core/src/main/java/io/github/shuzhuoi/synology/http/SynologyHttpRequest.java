package io.github.shuzhuoi.synology.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SDK 内部统一 HTTP 请求对象。
 * <p>
 * 业务客户端只构造这个抽象请求，具体由 Hutool、OkHttp 等适配器转换成真实 HTTP 请求。
 */
public class SynologyHttpRequest {

    /**
     * HTTP 方法，目前 File Station 高频接口主要使用 GET 和 POST。
     */
    private final SynologyHttpMethod method;
    /**
     * 完整请求地址，已经包含 DSM /webapi 路径。
     */
    private final String url;
    /**
     * URL 查询参数或普通表单参数。
     */
    private final Map<String, String> parameters;
    /**
     * multipart/form-data 的 part 列表，上传文件时使用。
     */
    private final List<SynologyMultipartPart> multipartParts;
    /**
     * 连接超时时间，单位毫秒。
     */
    private final int connectTimeoutMillis;
    /**
     * 读取超时时间，单位毫秒。
     */
    private final int readTimeoutMillis;
    /**
     * 响应体读取模式，默认按普通 JSON 文本读取。
     */
    private final ResponseBodyMode responseBodyMode;

    private SynologyHttpRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        this.parameters = Collections.unmodifiableMap(new LinkedHashMap<String, String>(builder.parameters));
        this.multipartParts = Collections.unmodifiableList(new ArrayList<SynologyMultipartPart>(builder.multipartParts));
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.responseBodyMode = builder.responseBodyMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SynologyHttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public List<SynologyMultipartPart> getMultipartParts() {
        return multipartParts;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public ResponseBodyMode getResponseBodyMode() {
        return responseBodyMode;
    }

    public boolean isMultipart() {
        return !multipartParts.isEmpty();
    }

    public static class Builder {
        private SynologyHttpMethod method = SynologyHttpMethod.GET;
        private String url;
        private Map<String, String> parameters = new LinkedHashMap<String, String>();
        private List<SynologyMultipartPart> multipartParts = new ArrayList<SynologyMultipartPart>();
        private int connectTimeoutMillis = 10000;
        private int readTimeoutMillis = 60000;
        private ResponseBodyMode responseBodyMode = ResponseBodyMode.TEXT;

        public Builder method(SynologyHttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder parameter(String name, String value) {
            if (value != null) {
                this.parameters.put(name, value);
            }
            return this;
        }

        public Builder parameters(Map<String, String> parameters) {
            if (parameters != null) {
                this.parameters.putAll(parameters);
            }
            return this;
        }

        public Builder multipartPart(SynologyMultipartPart part) {
            if (part != null) {
                this.multipartParts.add(part);
            }
            return this;
        }

        public Builder connectTimeoutMillis(int connectTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        public Builder readTimeoutMillis(int readTimeoutMillis) {
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }

        public Builder responseBodyMode(ResponseBodyMode responseBodyMode) {
            if (responseBodyMode != null) {
                this.responseBodyMode = responseBodyMode;
            }
            return this;
        }

        public SynologyHttpRequest build() {
            return new SynologyHttpRequest(this);
        }
    }
}
