package io.github.shuzhuoi.synology.http.hutool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;

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
            HttpResponse response = hutoolRequest.execute();
            if (isBinaryResponse(request)) {
                return new SynologyHttpResponse(response.getStatus(), response.headers(), null, response.bodyStream());
            }
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

    private boolean isBinaryResponse(SynologyHttpRequest request) {
        // 下载和缩略图接口都返回二进制内容，不能调用 body() 把响应读成字符串。
        String apiName = String.valueOf(request.getParameters().get("api"));
        return "SYNO.FileStation.Download".equals(apiName)
                || "SYNO.FileStation.Thumb".equals(apiName);
    }
}
