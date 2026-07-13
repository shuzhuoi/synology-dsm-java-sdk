package io.github.shuzhuoi.synology.internal.request;

import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一次 Synology WebAPI 调用的请求描述。
 * <p>
 * 该对象只描述 SDK 业务层需要表达的请求，不直接依赖 Hutool、OkHttp 等具体 HTTP 实现。
 * 当前先用于下载、缩略图和 multipart 上传试点。
 */
public class SynologyApiRequest {

    private final String path;
    private final String apiName;
    private final int version;
    private final String method;
    private final boolean authenticated;
    private final Map<String, String> parameters;
    private final Class<?> responseType;
    private final ResponseBodyMode responseBodyMode;
    private final SynologyHttpMethod httpMethod;
    private final List<SynologyMultipartPart> multipartParts;

    private SynologyApiRequest(Builder builder) {
        this.path = builder.path;
        this.apiName = builder.apiName;
        this.version = builder.version;
        this.method = builder.method;
        this.authenticated = builder.authenticated;
        this.parameters = Collections.unmodifiableMap(new LinkedHashMap<String, String>(builder.parameters));
        this.responseType = builder.responseType;
        this.responseBodyMode = builder.responseBodyMode;
        this.httpMethod = builder.httpMethod;
        this.multipartParts = Collections.unmodifiableList(new ArrayList<SynologyMultipartPart>(builder.multipartParts));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPath() {
        return path;
    }

    public String getApiName() {
        return apiName;
    }

    public int getVersion() {
        return version;
    }

    public String getMethod() {
        return method;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Class<?> getResponseType() {
        return responseType;
    }

    public ResponseBodyMode getResponseBodyMode() {
        return responseBodyMode;
    }

    public SynologyHttpMethod getHttpMethod() {
        return httpMethod;
    }

    public List<SynologyMultipartPart> getMultipartParts() {
        return multipartParts;
    }

    public boolean isMultipart() {
        return !multipartParts.isEmpty();
    }

    public static class Builder {
        private String path;
        private String apiName;
        private int version;
        private String method;
        private boolean authenticated;
        private Map<String, String> parameters = new LinkedHashMap<String, String>();
        private Class<?> responseType = Object.class;
        private ResponseBodyMode responseBodyMode = ResponseBodyMode.TEXT;
        private SynologyHttpMethod httpMethod = SynologyHttpMethod.GET;
        private List<SynologyMultipartPart> multipartParts = new ArrayList<SynologyMultipartPart>();

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder apiName(String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder authenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public Builder parameters(Map<String, String> parameters) {
            if (parameters != null) {
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    if (entry.getValue() != null) {
                        this.parameters.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return this;
        }

        public Builder parameter(String name, String value) {
            if (name != null && value != null) {
                this.parameters.put(name, value);
            }
            return this;
        }

        public Builder responseType(Class<?> responseType) {
            if (responseType != null) {
                this.responseType = responseType;
            }
            return this;
        }

        public Builder responseBodyMode(ResponseBodyMode responseBodyMode) {
            if (responseBodyMode != null) {
                this.responseBodyMode = responseBodyMode;
            }
            return this;
        }

        public Builder httpMethod(SynologyHttpMethod httpMethod) {
            if (httpMethod != null) {
                this.httpMethod = httpMethod;
            }
            return this;
        }

        public Builder multipartPart(SynologyMultipartPart multipartPart) {
            if (multipartPart != null) {
                this.multipartParts.add(multipartPart);
            }
            return this;
        }

        public SynologyApiRequest build() {
            if (path == null || apiName == null || method == null) {
                throw new IllegalStateException("path, apiName and method must not be null");
            }
            return new SynologyApiRequest(this);
        }
    }
}
