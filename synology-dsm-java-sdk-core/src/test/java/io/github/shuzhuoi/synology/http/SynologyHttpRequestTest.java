package io.github.shuzhuoi.synology.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 覆盖 SDK 统一 HTTP 请求对象的默认值，避免后续 adapter 改造破坏普通 JSON 请求。
 */
class SynologyHttpRequestTest {

    @Test
    void defaultsResponseBodyModeToText() {
        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .url("http://nas:5000/webapi/entry.cgi")
                .build();

        assertEquals(ResponseBodyMode.TEXT, request.getResponseBodyMode());
    }

    @Test
    void canSetResponseBodyModeToStream() {
        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .url("http://nas:5000/webapi/entry.cgi")
                .responseBodyMode(ResponseBodyMode.STREAM)
                .build();

        assertEquals(ResponseBodyMode.STREAM, request.getResponseBodyMode());
    }
}
