package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyApiException;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 覆盖执行器当前 JSON 响应解析行为，为后续拆分 ResponseParser 提供回归保护。
 */
class SynologyApiExecutorTest {

    @Test
    void getPublicConvertsDataAndBuildsBaseRequestParameters() {
        FakeSynologyHttpClient httpClient = new FakeSynologyHttpClient(
                "{\"success\":true,\"data\":{\"name\":\"alpha\",\"count\":7}}");
        SynologyApiExecutor executor = newExecutor(httpClient);

        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("keyword", "photo");
        parameters.put("ignored", null);

        ExecutorData data = executor.getPublic("entry.cgi", "SYNO.Test", 1, "list", parameters, ExecutorData.class);

        assertEquals("alpha", data.getName());
        assertEquals(Integer.valueOf(7), data.getCount());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertEquals(SynologyHttpMethod.GET, request.getMethod());
        assertEquals("http://nas:5000/webapi/entry.cgi", request.getUrl());
        assertEquals("SYNO.Test", request.getParameters().get("api"));
        assertEquals("1", request.getParameters().get("version"));
        assertEquals("list", request.getParameters().get("method"));
        assertEquals("photo", request.getParameters().get("keyword"));
        assertFalse(request.getParameters().containsKey("ignored"));
    }

    @Test
    void getPublicMapConvertsDataNodeToLinkedHashMap() {
        FakeSynologyHttpClient httpClient = new FakeSynologyHttpClient(
                "{\"success\":true,\"data\":{\"first\":{\"name\":\"alpha\",\"count\":1}}}");
        SynologyApiExecutor executor = newExecutor(httpClient);

        Map<String, ExecutorData> data = executor.getPublicMap("entry.cgi", "SYNO.Test", 1, "map", null, ExecutorData.class);

        assertEquals("alpha", data.get("first").getName());
        assertEquals(Integer.valueOf(1), data.get("first").getCount());
    }

    @Test
    void getPublicReturnsNullForObjectType() {
        FakeSynologyHttpClient httpClient = new FakeSynologyHttpClient(
                "{\"success\":true,\"data\":{\"name\":\"alpha\"}}");
        SynologyApiExecutor executor = newExecutor(httpClient);

        Object data = executor.getPublic("entry.cgi", "SYNO.Test", 1, "noop", null, Object.class);

        assertNull(data);
    }

    @Test
    void throwsApiExceptionWhenDsmReportsFailure() {
        FakeSynologyHttpClient httpClient = new FakeSynologyHttpClient(
                "{\"success\":false,\"error\":{\"code\":119}}");
        SynologyApiExecutor executor = newExecutor(httpClient);

        SynologyApiException exception = assertThrows(SynologyApiException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                executor.getPublic("entry.cgi", "SYNO.Test", 1, "list", null, ExecutorData.class);
            }
        });

        assertEquals(Integer.valueOf(119), exception.getErrorCode());
        assertEquals("SYNO.Test", exception.getApiName());
        assertEquals("list", exception.getMethod());
    }

    @Test
    void throwsHttpExceptionWhenResponseBodyIsEmpty() {
        FakeSynologyHttpClient httpClient = new FakeSynologyHttpClient(" ");
        SynologyApiExecutor executor = newExecutor(httpClient);

        assertThrows(SynologyHttpException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                executor.getPublic("entry.cgi", "SYNO.Test", 1, "list", null, ExecutorData.class);
            }
        });
    }

    @Test
    void throwsDsmExceptionWhenResponseBodyIsNotJson() {
        FakeSynologyHttpClient httpClient = new FakeSynologyHttpClient("not-json");
        SynologyApiExecutor executor = newExecutor(httpClient);

        assertThrows(SynologyDsmException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                executor.getPublic("entry.cgi", "SYNO.Test", 1, "list", null, ExecutorData.class);
            }
        });
    }

    private SynologyApiExecutor newExecutor(FakeSynologyHttpClient httpClient) {
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        return new SynologyApiExecutor(config, httpClient);
    }
}
