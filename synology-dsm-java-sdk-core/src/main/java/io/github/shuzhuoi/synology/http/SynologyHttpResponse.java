package io.github.shuzhuoi.synology.http;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SDK 内部统一 HTTP 响应对象。
 */
public class SynologyHttpResponse {

    /**
     * HTTP 状态码。
     */
    private final int statusCode;
    /**
     * HTTP 响应头。
     */
    private final Map<String, List<String>> headers;
    /**
     * 文本响应体，普通 JSON API 使用。
     */
    private final String body;
    /**
     * 二进制响应流，下载文件时使用。
     */
    private final InputStream bodyStream;

    public SynologyHttpResponse(int statusCode, Map<String, List<String>> headers, String body, InputStream bodyStream) {
        this.statusCode = statusCode;
        this.headers = headers == null ? Collections.<String, List<String>>emptyMap() : headers;
        this.body = body;
        this.bodyStream = bodyStream;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public InputStream getBodyStream() {
        return bodyStream;
    }
}
