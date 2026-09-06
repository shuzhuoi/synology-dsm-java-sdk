package io.github.shuzhuoi.synology.docker;

import io.github.shuzhuoi.synology.auth.AuthClient;
import io.github.shuzhuoi.synology.auth.SynologySession;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.docker.container.DockerContainerListRequest;
import io.github.shuzhuoi.synology.docker.container.DockerContainerType;
import io.github.shuzhuoi.synology.docker.log.DockerContainerLogRequest;
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
 * Docker 各子客户端的契约测试。
 * <p>
 * 通过记录型 HTTP fake 捕获最终发出的请求，逐个断言各客户端使用的
 * SYNO.Docker.Container / SYNO.Docker.Container.Resource / SYNO.Docker.Container.Log
 * API 名称、版本、method 和关键参数（含 JSON 引号编码规则），
 * 防止重构时悄悄破坏 DSM Container Manager 契约。
 * 注意：版本号推定自社区文档与真机 SYNO.API.Info 抓取，真机验证由 DockerCoverageExample 负责。
 */
class DockerClientContractTest {

    private static final String SID = "sid-test";

    private RecordingSynologyHttpClient httpClient;
    private DockerClient docker;

    @BeforeEach
    void setUp() {
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        httpClient = new RecordingSynologyHttpClient();
        SynologyApiExecutor executor = new SynologyApiExecutor(config, httpClient, new SuccessJsonCodec());
        executor.setSessionManager(new FixedSidSessionManager(config));
        docker = new DockerClient(executor);
    }

    @Test
    void listUsesContainerApiV1WithPagination() {
        docker.container().list(DockerContainerListRequest.builder()
                .offset(0)
                .limit(-1)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Container", "list");
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("-1", request.getParameters().get("limit"));
        // 未设置类型过滤时不携带 type 参数。
        assertNull(request.getParameters().get("type"));
    }

    @Test
    void listWithTypeFilterSendsTypeParameter() {
        docker.container().list(DockerContainerListRequest.builder()
                .type(DockerContainerType.RUNNING)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Container", "list");
        assertEquals("running", request.getParameters().get("type"));
    }

    @Test
    void getUsesContainerApiWithJsonQuotedName() {
        docker.container().get("nginx-proxy");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Container", "get");
        // Container Manager 契约声明 requestFormat=JSON，name 参数带 JSON 引号。
        assertEquals("\"nginx-proxy\"", request.getParameters().get("name"));
    }

    @Test
    void lifecycleOperationsUseContainerApiWithQuotedName() {
        docker.container().start("jellyfin");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Container", "start");
        assertEquals("\"jellyfin\"", httpClient.getLastRequest().getParameters().get("name"));

        docker.container().stop("jellyfin");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Container", "stop");
        assertEquals("\"jellyfin\"", httpClient.getLastRequest().getParameters().get("name"));

        docker.container().restart("jellyfin");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Container", "restart");
        assertEquals("\"jellyfin\"", httpClient.getLastRequest().getParameters().get("name"));
    }

    @Test
    void deleteSendsForceAndPreserveProfileParameters() {
        docker.container().delete("jellyfin", Boolean.TRUE, Boolean.FALSE);

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Container", "delete");
        assertEquals("\"jellyfin\"", request.getParameters().get("name"));
        // force / preserve_profile 为普通小写布尔字符串。
        assertEquals("true", request.getParameters().get("force"));
        assertEquals("false", request.getParameters().get("preserve_profile"));
    }

    @Test
    void resourceGetUsesContainerResourceApiV1() {
        docker.resource().get();

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Container.Resource", "get");
        // get 无业务参数。
        assertNull(request.getParameters().get("name"));
    }

    @Test
    void logGetUsesContainerLogApiWithFilterParameters() {
        docker.log().get(DockerContainerLogRequest.builder("watchtower")
                .keyword("error")
                .sortDir("DESC")
                .offset(0)
                .limit(100)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Container.Log", "get");
        // 字符串过滤参数带 JSON 引号，分页参数为普通字符串。
        assertEquals("\"watchtower\"", request.getParameters().get("name"));
        assertEquals("\"error\"", request.getParameters().get("keyword"));
        assertEquals("\"DESC\"", request.getParameters().get("sort_dir"));
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("100", request.getParameters().get("limit"));
        // 未设置的时间参数不携带。
        assertNull(request.getParameters().get("from"));
        assertNull(request.getParameters().get("to"));
    }

    private void assertApi(SynologyHttpRequest request, String expectedApi, String expectedMethod) {
        assertEquals("http://nas:5000/webapi/entry.cgi", request.getUrl());
        assertEquals(expectedApi, request.getParameters().get("api"));
        assertEquals("1", request.getParameters().get("version"));
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
