package io.github.shuzhuoi.synology.core.system;

import io.github.shuzhuoi.synology.auth.AuthClient;
import io.github.shuzhuoi.synology.auth.SynologySession;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Core System 各子客户端的契约测试。
 * <p>
 * 通过记录型 HTTP fake 捕获最终发出的请求，逐个断言各客户端使用的
 * SYNO.Core.System / SYNO.Core.System.Utilization API 名称、版本、method 和关键参数，
 * 防止重构时悄悄破坏 DSM WebAPI 契约。
 * 注意：版本号推定自社区文档，真机验证由 CoreSystemCoverageExample 负责。
 */
class CoreSystemClientContractTest {

    private static final String SID = "sid-test";

    private RecordingSynologyHttpClient httpClient;
    private CoreSystemClient coreSystem;

    @BeforeEach
    void setUp() {
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        httpClient = new RecordingSynologyHttpClient();
        SynologyApiExecutor executor = new SynologyApiExecutor(config, httpClient, new SuccessJsonCodec());
        executor.setSessionManager(new FixedSidSessionManager(config));
        coreSystem = new CoreSystemClient(executor);
    }

    @Test
    void infoUsesCoreSystemApiV3Info() {
        coreSystem.info().getInfo();

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Core.System", "3", "info");
        // info 无业务参数。
        assertNull(request.getParameters().get("type"));
    }

    @Test
    void utilizationGetUsesUtilizationApiV1() {
        coreSystem.utilization().get();

        assertApi(httpClient.getLastRequest(), "SYNO.Core.System.Utilization", "1", "get");
    }

    @Test
    void shutdownUsesPowerVersionWithLocalTrue() {
        coreSystem.info().shutdown();

        SynologyHttpRequest request = httpClient.getLastRequest();
        // 官方契约中电源操作仅存在于 SYNO.Core.System v1。
        assertApi(request, "SYNO.Core.System", "1", "shutdown");
        assertEquals("true", request.getParameters().get("local"));
    }

    @Test
    void rebootUsesPowerVersionWithoutExtraParameters() {
        coreSystem.info().reboot();

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Core.System", "1", "reboot");
        assertNull(request.getParameters().get("local"));
    }

    private void assertApi(SynologyHttpRequest request, String expectedApi, String expectedVersion, String expectedMethod) {
        assertEquals("http://nas:5000/webapi/entry.cgi", request.getUrl());
        assertEquals(expectedApi, request.getParameters().get("api"));
        assertEquals(expectedVersion, request.getParameters().get("version"));
        assertEquals(expectedMethod, request.getParameters().get("method"));
        assertEquals(SID, request.getParameters().get("_sid"));
    }

    /**
     * 记录全部请求并固定返回 success 响应的 HTTP fake。
     * <p>
     * 契约测试只断言请求内容，响应统一返回 success。
     */
    private static class RecordingSynologyHttpClient implements SynologyHttpClient {

        private final List<SynologyHttpRequest> requests = new ArrayList<SynologyHttpRequest>();

        @Override
        public SynologyHttpResponse execute(SynologyHttpRequest request) {
            requests.add(request);
            return new SynologyHttpResponse(200, null, "{\"success\":true}", new ByteArrayInputStream(new byte[0]));
        }

        SynologyHttpRequest getLastRequest() {
            return requests.get(requests.size() - 1);
        }
    }

    /**
     * 返回固定 SID 的会话管理器，避免契约测试触发真实登录流程。
     */
    private static class FixedSidSessionManager extends SynologySessionManager {

        private final SynologySession session;

        FixedSidSessionManager(SynologyDsmConfig config) {
            super(config, (AuthClient) null);
            this.session = new SynologySession(SID, config.getSessionName(), null);
        }

        @Override
        public synchronized SynologySession currentSession() {
            return session;
        }
    }

    /**
     * 契约测试用最小 JSON fake：统一返回 success 且 data 为空。
     */
    private static class SuccessJsonCodec implements SynologyJsonCodec {

        @Override
        public <T> SynologyJsonResponse<T> decode(String body, Class<T> dataType) {
            return new SynologyJsonResponse<T>(Boolean.TRUE, null, null);
        }

        @Override
        public <T> SynologyJsonResponse<Map<String, T>> decodeMap(String body, Class<T> valueType) {
            return new SynologyJsonResponse<Map<String, T>>(Boolean.TRUE, null, null);
        }
    }
}
