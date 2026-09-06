package io.github.shuzhuoi.synology.docker;

import io.github.shuzhuoi.synology.auth.AuthClient;
import io.github.shuzhuoi.synology.auth.SynologySession;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.docker.container.DockerContainerListRequest;
import io.github.shuzhuoi.synology.docker.container.DockerContainerType;
import io.github.shuzhuoi.synology.docker.image.DockerImageListRequest;
import io.github.shuzhuoi.synology.docker.log.DockerContainerLogRequest;
import io.github.shuzhuoi.synology.docker.model.DockerNetwork;
import io.github.shuzhuoi.synology.docker.network.DockerNetworkCreateRequest;
import io.github.shuzhuoi.synology.docker.registry.DockerRegistryUpsertRequest;
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
 * SYNO.Docker.Container / SYNO.Docker.Container.Resource / SYNO.Docker.Container.Log /
 * SYNO.Docker.Image / SYNO.Docker.Project / SYNO.Docker.Network / SYNO.Docker.Registry
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

    @Test
    void imageListUsesImageApiV1WithPagination() {
        docker.image().list(DockerImageListRequest.builder()
                .offset(0)
                .limit(-1)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Image", "list");
        // offset / limit 为普通整数字符串，show_dsm 未设置时不携带。
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("-1", request.getParameters().get("limit"));
        assertNull(request.getParameters().get("show_dsm"));
    }

    @Test
    void imageListSendsShowDsmAsPlainBoolean() {
        docker.image().list(DockerImageListRequest.builder()
                .showDsm(Boolean.TRUE)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Image", "list");
        // show_dsm 为普通小写布尔字符串。
        assertEquals("true", request.getParameters().get("show_dsm"));
    }

    @Test
    void imageGetUsesImageApiWithQuotedRepositoryTag() {
        docker.image().get("grafana/grafana", "11.1.0");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Image", "get");
        // image 参数为「仓库:标签」组合，以 JSON 字符串形式传输（带引号）。
        assertEquals("\"grafana/grafana:11.1.0\"", request.getParameters().get("image"));
    }

    @Test
    void imageDeleteUsesImageApiWithQuotedNameAndTag() {
        docker.image().delete("nginx", "alpine");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Image", "delete");
        // name / tag 均以 JSON 字符串形式传输（带引号）。
        assertEquals("\"nginx\"", request.getParameters().get("name"));
        assertEquals("\"alpine\"", request.getParameters().get("tag"));
    }

    @Test
    void imagePruneUsesImageApiWithoutBusinessParameters() {
        docker.image().prune();

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Image", "prune");
        // prune 无业务参数。
        assertNull(request.getParameters().get("name"));
        assertNull(request.getParameters().get("tag"));
    }

    @Test
    void imagePullStartUsesImageApiWithQuotedRepositoryAndTag() {
        docker.image().pullStart("nginx", "latest");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Image", "pull_start");
        // repository / tag 以 JSON 字符串形式传输（带引号）。
        assertEquals("\"nginx\"", request.getParameters().get("repository"));
        assertEquals("\"latest\"", request.getParameters().get("tag"));
    }

    @Test
    void imagePullStatusUsesImageApiWithQuotedTaskId() {
        docker.image().pullStatus("task-123");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Image", "pull_status");
        // task_id 以 JSON 字符串形式传输（带引号）。
        assertEquals("\"task-123\"", request.getParameters().get("task_id"));
    }

    @Test
    void projectListUsesProjectApiWithoutBusinessParameters() {
        docker.project().list();

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Project", "list");
        // list 无业务参数，data 是以项目 UUID 为键的 map（由 decodeMap 解析）。
        assertNull(request.getParameters().get("id"));
    }

    @Test
    void projectGetUsesProjectApiWithQuotedId() {
        docker.project().get("187b2816-fd6c-4f87-b178-6d94806c7404");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Project", "get");
        // id 以 JSON 字符串形式传输（带引号）。
        assertEquals("\"187b2816-fd6c-4f87-b178-6d94806c7404\"", request.getParameters().get("id"));
    }

    @Test
    void projectLifecycleOperationsUseProjectApiWithQuotedId() {
        docker.project().start("187b2816-fd6c-4f87-b178-6d94806c7404");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Project", "start");
        assertEquals("\"187b2816-fd6c-4f87-b178-6d94806c7404\"", httpClient.getLastRequest().getParameters().get("id"));

        docker.project().stop("187b2816-fd6c-4f87-b178-6d94806c7404");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Project", "stop");
        assertEquals("\"187b2816-fd6c-4f87-b178-6d94806c7404\"", httpClient.getLastRequest().getParameters().get("id"));

        docker.project().restart("187b2816-fd6c-4f87-b178-6d94806c7404");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Project", "restart");
        assertEquals("\"187b2816-fd6c-4f87-b178-6d94806c7404\"", httpClient.getLastRequest().getParameters().get("id"));

        docker.project().clean("187b2816-fd6c-4f87-b178-6d94806c7404");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Project", "clean");
        assertEquals("\"187b2816-fd6c-4f87-b178-6d94806c7404\"", httpClient.getLastRequest().getParameters().get("id"));
    }

    @Test
    void projectDeleteSendsIdAndPreserveContentParameters() {
        docker.project().delete("187b2816-fd6c-4f87-b178-6d94806c7404", Boolean.TRUE);

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Project", "delete");
        // id 以 JSON 字符串形式传输（带引号），preserve_content 为普通小写布尔字符串。
        assertEquals("\"187b2816-fd6c-4f87-b178-6d94806c7404\"", request.getParameters().get("id"));
        assertEquals("true", request.getParameters().get("preserve_content"));
    }

    @Test
    void networkListUsesNetworkApiWithoutBusinessParameters() {
        docker.network().list();

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Network", "list");
        // list 无业务参数。
        assertNull(request.getParameters().get("name"));
    }

    @Test
    void networkCreateSendsQuotedStringsAndPlainBoolean() {
        docker.network().create(DockerNetworkCreateRequest.builder("app_net")
                .driver("bridge")
                .enableIpv6(Boolean.FALSE)
                .subnet("172.28.0.0/16")
                .gateway("172.28.0.1")
                .iprange("172.28.0.0/24")
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Network", "create");
        // 字符串参数以 JSON 字符串形式传输（带引号），enable_ipv6 为普通小写布尔字符串。
        assertEquals("\"app_net\"", request.getParameters().get("name"));
        assertEquals("\"bridge\"", request.getParameters().get("driver"));
        assertEquals("false", request.getParameters().get("enable_ipv6"));
        assertEquals("\"172.28.0.0/16\"", request.getParameters().get("subnet"));
        assertEquals("\"172.28.0.1\"", request.getParameters().get("gateway"));
        // 注意参数名 iprange 无下划线。
        assertEquals("\"172.28.0.0/24\"", request.getParameters().get("iprange"));
    }

    @Test
    void networkRemoveSendsNetworksJsonArrayParameter() {
        DockerNetwork network = new DockerNetwork();
        network.setId("b741915823aac");
        network.setName("vault_default");
        network.setDriver("bridge");
        network.setEnableIpv6(Boolean.FALSE);
        network.setGateway("172.22.0.1");
        network.setIprange("");
        network.setSubnet("172.22.0.0/16");
        network.setContainers(java.util.Collections.singletonList("vault"));

        docker.network().remove(network);

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Network", "remove");
        // networks 参数为 JSON 数组字符串，元素是完整网络对象（字段顺序与 list 响应一致）。
        assertEquals("[{\"containers\":[\"vault\"],\"driver\":\"bridge\",\"enable_ipv6\":false,"
                        + "\"gateway\":\"172.22.0.1\",\"id\":\"b741915823aac\",\"iprange\":\"\","
                        + "\"name\":\"vault_default\",\"subnet\":\"172.22.0.0/16\"}]",
                request.getParameters().get("networks"));
    }

    @Test
    void registryGetUsesRegistryApiV1WithoutBusinessParameters() {
        docker.registry().get();

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Registry", "get");
        // get 无业务参数。
        assertNull(request.getParameters().get("name"));
    }

    @Test
    void registrySearchSendsQuotedKeywordAndPagination() {
        docker.registry().search("caddy", 0, 50);

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Registry", "search");
        // q 为搜索关键词（带引号）；offset / limit 为普通整数字符串。
        assertEquals("\"caddy\"", request.getParameters().get("q"));
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("50", request.getParameters().get("limit"));
    }

    @Test
    void registryTagsUsesRegistryApiV2WithQuotedRepository() {
        docker.registry().tags("postgres", 0, 20);

        SynologyHttpRequest request = httpClient.getLastRequest();
        // 注意 tags 使用 Registry v2 版本（分页增强版），与同 API 其他方法的 v1 不同。
        assertApi(request, "SYNO.Docker.Registry", "2", "tags");
        assertEquals("\"postgres\"", request.getParameters().get("repository"));
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("20", request.getParameters().get("limit"));
    }

    @Test
    void registryCreateSendsUpsertParameters() {
        docker.registry().create(DockerRegistryUpsertRequest.builder("ghcr", "https://ghcr.io")
                .enableTrustSsc(Boolean.TRUE)
                .username("user")
                .password("pass")
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.Docker.Registry", "create");
        // 字符串参数带引号；enable_trust_SSC（注意大写 SSC）为普通小写布尔字符串。
        assertEquals("\"ghcr\"", request.getParameters().get("name"));
        assertEquals("\"https://ghcr.io\"", request.getParameters().get("url"));
        assertEquals("true", request.getParameters().get("enable_trust_SSC"));
        assertEquals("\"user\"", request.getParameters().get("username"));
        assertEquals("\"pass\"", request.getParameters().get("password"));
    }

    @Test
    void registryUsingAndDeleteSendQuotedName() {
        docker.registry().using("ghcr");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Registry", "using");
        assertEquals("\"ghcr\"", httpClient.getLastRequest().getParameters().get("name"));

        docker.registry().delete("ghcr");
        assertApi(httpClient.getLastRequest(), "SYNO.Docker.Registry", "delete");
        assertEquals("\"ghcr\"", httpClient.getLastRequest().getParameters().get("name"));
    }

    private void assertApi(SynologyHttpRequest request, String expectedApi, String expectedMethod) {
        assertApi(request, expectedApi, "1", expectedMethod);
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
