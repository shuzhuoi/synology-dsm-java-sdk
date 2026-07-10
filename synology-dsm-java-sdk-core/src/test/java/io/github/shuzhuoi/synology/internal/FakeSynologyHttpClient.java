package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;

/**
 * 执行器测试用 HTTP 客户端，避免单元测试请求真实 DSM。
 */
class FakeSynologyHttpClient implements SynologyHttpClient {

    private final SynologyHttpResponse response;
    private SynologyHttpRequest lastRequest;

    FakeSynologyHttpClient(String body) {
        this.response = new SynologyHttpResponse(200, null, body, null);
    }

    @Override
    public SynologyHttpResponse execute(SynologyHttpRequest request) {
        // 记录最后一次请求，便于断言执行器是否正确合并基础参数。
        this.lastRequest = request;
        return response;
    }

    SynologyHttpRequest getLastRequest() {
        return lastRequest;
    }
}
