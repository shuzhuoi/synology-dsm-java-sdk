package io.github.shuzhuoi.synology.http.hutool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 Hutool HTTP 的默认实现，适合 Java 8 环境。
 */
public class HutoolSynologyHttpClient implements SynologyHttpClient {

    @Override
    public SynologyHttpResponse execute(SynologyHttpRequest request) {
        try {
            HttpRequest hutoolRequest = createRequest(request);
            hutoolRequest.timeout(request.getReadTimeoutMillis());
            appendParameters(hutoolRequest, request);
            if (request.getResponseBodyMode() == ResponseBodyMode.STREAM) {
                // 流式响应必须用 executeAsync：同步 execute 会把响应体全量读入内存，
                // 下载大文件或缩略图时会造成内存暴涨；异步模式保留底层连接流。
                HttpResponse response = hutoolRequest.executeAsync();
                return new SynologyHttpResponse(response.getStatus(), response.headers(), null, wrapStream(response));
            }
            HttpResponse response = hutoolRequest.execute();
            return new SynologyHttpResponse(response.getStatus(), response.headers(), response.body(), null);
        } catch (RuntimeException e) {
            throw new SynologyHttpException("failed to execute Synology HTTP request", e);
        }
    }

    private HttpRequest createRequest(SynologyHttpRequest request) {
        if (request.getMethod() == SynologyHttpMethod.POST || request.isMultipart()) {
            return HttpRequest.post(request.getUrl());
        }
        return HttpRequest.get(request.getUrl());
    }

    private void appendParameters(HttpRequest hutoolRequest, SynologyHttpRequest request) {
        // Hutool 会根据 form 中的 File 自动构造 multipart/form-data。
        Map<String, Object> form = new LinkedHashMap<String, Object>();
        form.putAll(request.getParameters());
        for (SynologyMultipartPart part : request.getMultipartParts()) {
            form.put(part.getName(), part.getValue());
        }
        if (!form.isEmpty()) {
            hutoolRequest.form(form);
        }
    }

    /**
     * 包装底层响应流：无响应体（204/205 或内容长度为 0）时返回 null 流并释放连接，
     * 与 OkHttp3 适配层行为保持一致。
     */
    private InputStream wrapStream(HttpResponse response) {
        int status = response.getStatus();
        if (status == 204 || status == 205 || response.contentLength() == 0) {
            response.close();
            return null;
        }
        return new HttpResponseInputStream(response);
    }

    /**
     * 在调用方关闭流的同时关闭底层 HttpResponse，连接才能及时归还连接池。
     */
    private static class HttpResponseInputStream extends FilterInputStream {

        private final HttpResponse response;

        HttpResponseInputStream(HttpResponse response) {
            super(response.bodyStream());
            this.response = response;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                response.close();
            }
        }
    }
}
