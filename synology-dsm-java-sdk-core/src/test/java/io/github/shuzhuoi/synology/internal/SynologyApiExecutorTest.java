package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.auth.AuthClient;
import io.github.shuzhuoi.synology.auth.SynologySession;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyApiException;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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

    @Test
    void downloadAuthenticatedBuildsStreamRequest() {
        ByteArrayInputStream bodyStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        FakeSynologyHttpClient httpClient = new FakeSynologyHttpClient(bodyStream);
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        SynologyApiExecutor executor = new SynologyApiExecutor(config, httpClient);
        executor.setSessionManager(new FixedSessionManager(config, "sid-1"));

        SynologyHttpResponse response = executor.downloadAuthenticated(
                "entry.cgi",
                "SYNO.FileStation.Download",
                2,
                "download",
                null
        );

        assertSame(bodyStream, response.getBodyStream());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertEquals(SynologyHttpMethod.GET, request.getMethod());
        assertEquals(ResponseBodyMode.STREAM, request.getResponseBodyMode());
        assertEquals("SYNO.FileStation.Download", request.getParameters().get("api"));
        assertEquals("sid-1", request.getParameters().get("_sid"));
    }

    @Test
    void getAuthenticatedRefreshesSessionAndRetriesOnceWhenSessionExpired() {
        SequencedSynologyHttpClient httpClient = new SequencedSynologyHttpClient(Arrays.asList(
                new SynologyHttpResponse(200, null, "{\"success\":false,\"error\":{\"code\":119}}", null),
                new SynologyHttpResponse(200, null, "{\"success\":true,\"data\":{\"name\":\"alpha\",\"count\":9}}", null)
        ));
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        SynologyApiExecutor executor = new SynologyApiExecutor(config, httpClient);
        RefreshingSessionManager sessionManager = new RefreshingSessionManager(config);
        executor.setSessionManager(sessionManager);
        executor.setAutoRefreshSession(true);

        ExecutorData data = executor.getAuthenticated("entry.cgi", "SYNO.Test", 1, "list", null, ExecutorData.class);

        assertEquals("alpha", data.getName());
        assertEquals(Integer.valueOf(9), data.getCount());
        assertEquals(1, sessionManager.getRefreshCount());
        assertEquals("sid-old", httpClient.getRequests().get(0).getParameters().get("_sid"));
        assertEquals("sid-new", httpClient.getRequests().get(1).getParameters().get("_sid"));
    }

    private SynologyApiExecutor newExecutor(FakeSynologyHttpClient httpClient) {
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        return new SynologyApiExecutor(config, httpClient);
    }

    /**
     * 固定 SID 的测试会话管理器，用于验证认证请求参数，不触发真实登录。
     */
    private static class FixedSessionManager extends SynologySessionManager {

        private final SynologySession session;

        FixedSessionManager(SynologyDsmConfig config, String sid) {
            super(config, (AuthClient) null);
            this.session = new SynologySession(sid, config.getSessionName(), null);
        }

        @Override
        public synchronized SynologySession currentSession() {
            return session;
        }
    }

    /**
     * 可刷新 SID 的测试会话管理器，用于验证 executor 自动刷新后是否使用新 SID 重试。
     */
    private static class RefreshingSessionManager extends SynologySessionManager {

        private final SynologyDsmConfig config;
        private SynologySession session;
        private int refreshCount;

        RefreshingSessionManager(SynologyDsmConfig config) {
            super(config, (AuthClient) null);
            this.config = config;
            this.session = new SynologySession("sid-old", config.getSessionName(), null);
        }

        @Override
        public synchronized SynologySession currentSession() {
            return session;
        }

        @Override
        public synchronized SynologySession refresh() {
            refreshCount++;
            session = new SynologySession("sid-new", config.getSessionName(), null);
            return session;
        }

        int getRefreshCount() {
            return refreshCount;
        }
    }

    /**
     * 按顺序返回响应的测试 HTTP 客户端，用于模拟第一次 SID 失效、第二次成功。
     */
    private static class SequencedSynologyHttpClient implements io.github.shuzhuoi.synology.http.SynologyHttpClient {

        private final List<SynologyHttpResponse> responses;
        private final List<SynologyHttpRequest> requests = new ArrayList<SynologyHttpRequest>();

        SequencedSynologyHttpClient(List<SynologyHttpResponse> responses) {
            this.responses = responses;
        }

        @Override
        public SynologyHttpResponse execute(SynologyHttpRequest request) {
            requests.add(request);
            return responses.get(Math.min(requests.size() - 1, responses.size() - 1));
        }

        List<SynologyHttpRequest> getRequests() {
            return requests;
        }
    }
}
