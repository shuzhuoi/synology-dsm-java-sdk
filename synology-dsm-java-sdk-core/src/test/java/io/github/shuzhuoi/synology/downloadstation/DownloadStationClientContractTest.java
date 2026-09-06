package io.github.shuzhuoi.synology.downloadstation;

import io.github.shuzhuoi.synology.auth.AuthClient;
import io.github.shuzhuoi.synology.auth.SynologySession;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.downloadstation.btsearch.BtSearchListRequest;
import io.github.shuzhuoi.synology.downloadstation.info.DownloadStationConfigRequest;
import io.github.shuzhuoi.synology.downloadstation.model.TaskAdditionalField;
import io.github.shuzhuoi.synology.downloadstation.rss.RssFeedListRequest;
import io.github.shuzhuoi.synology.downloadstation.rss.RssSiteListRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskCreateRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskCreateTorrentRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskGetInfoRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskListRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Download Station 各子客户端的契约测试。
 * <p>
 * 通过记录型 HTTP fake 捕获最终发出的请求，逐个断言各客户端使用的
 * SYNO.DownloadStation2.* API 名称、版本、method 和关键参数编码
 * （id 逗号分隔、taskid 引号包裹、additional JSON 数组、目的地引号包裹等），
 * 防止重构时悄悄破坏 DSM 7 的 WebAPI 契约。
 * 注意：版本号推定自社区文档，真机验证由 DownloadStationCoverageExample 负责。
 */
class DownloadStationClientContractTest {

    private static final String SID = "sid-test";

    private RecordingSynologyHttpClient httpClient;
    private DownloadStationClient downloadStation;
    private File tempTorrentFile;

    @BeforeEach
    void setUp() throws IOException {
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        httpClient = new RecordingSynologyHttpClient();
        SynologyApiExecutor executor = new SynologyApiExecutor(config, httpClient, new SuccessJsonCodec());
        executor.setSessionManager(new FixedSidSessionManager(config));
        downloadStation = new DownloadStationClient(executor);
        tempTorrentFile = File.createTempFile("sdk-ds-contract-", ".torrent");
        Files.write(tempTorrentFile.toPath(), "contract test torrent".getBytes("UTF-8"));
    }

    @AfterEach
    void tearDown() {
        if (tempTorrentFile != null && tempTorrentFile.exists()) {
            tempTorrentFile.delete();
        }
    }

    @Test
    void infoGetInfoUsesInfoApi() {
        downloadStation.info().getInfo();

        assertApi(httpClient.getLastRequest(), "SYNO.DownloadStation2.Info", "3", "getInfo");
    }

    @Test
    void infoGetConfigUsesInfoApi() {
        downloadStation.info().getConfig();

        assertApi(httpClient.getLastRequest(), "SYNO.DownloadStation2.Info", "3", "getConfig");
    }

    @Test
    void infoSetServerConfigEncodesConfigFields() {
        downloadStation.info().setServerConfig(DownloadStationConfigRequest.builder()
                .btMaxDownload(1024)
                .emuleEnabled(false)
                .defaultDestination("/downloads")
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Info", "3", "setServerConfig");
        assertEquals("1024", request.getParameters().get("bt_max_download"));
        assertEquals("false", request.getParameters().get("emule_enabled"));
        // 未设置的字段不下发，保持 DSM 当前值。
        assertNull(request.getParameters().get("nzb_max_download"));
        // 目的地路径官方要求 JSON 字符串形式，即带引号。
        assertEquals("\"/downloads\"", request.getParameters().get("default_destination"));
    }

    @Test
    void taskListEncodesPagingAndAdditional() {
        downloadStation.task().list(TaskListRequest.builder()
                .offset(0)
                .limit(10)
                .addAdditional(TaskAdditionalField.DETAIL)
                .addAdditional(TaskAdditionalField.TRANSFER)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task", "3", "list");
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("10", request.getParameters().get("limit"));
        assertEquals("[\"detail\",\"transfer\"]", request.getParameters().get("additional"));
    }

    @Test
    void taskGetInfoEncodesCommaSeparatedIdsAndAdditional() {
        downloadStation.task().getInfo(TaskGetInfoRequest.builder("dbid_1")
                .addId("dbid_2")
                .addAdditional(TaskAdditionalField.TRANSFER)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task", "3", "getinfo");
        // 官方约定多个任务 ID 以逗号分隔，不使用 JSON 引号。
        assertEquals("dbid_1,dbid_2", request.getParameters().get("id"));
        assertEquals("[\"transfer\"]", request.getParameters().get("additional"));
    }

    @Test
    void taskCreateEncodesUriDestinationAndOptions() {
        downloadStation.task().create(TaskCreateRequest.builder("http://example.com/a.iso")
                .addUri("magnet:?xt=urn:btih:abc")
                .destination("/downloads")
                .unzipPassword("pw")
                .createList(false)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task", "3", "create");
        // 多个下载链接以逗号分隔。
        assertEquals("http://example.com/a.iso,magnet:?xt=urn:btih:abc", request.getParameters().get("uri"));
        // 目的地路径官方要求 JSON 字符串形式，即带引号。
        assertEquals("\"/downloads\"", request.getParameters().get("destination"));
        assertEquals("pw", request.getParameters().get("unzip_password"));
        assertEquals("false", request.getParameters().get("create_list"));
    }

    @Test
    void taskCreateTorrentBuildsMultipartPost() {
        downloadStation.task().createTorrent(TaskCreateTorrentRequest.builder(tempTorrentFile)
                .destination("/downloads")
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertEquals(SynologyHttpMethod.POST, request.getMethod());
        assertEquals("SYNO.DownloadStation2.Task", request.getParameters().get("api"));
        assertEquals("3", request.getParameters().get("version"));
        assertEquals("create", request.getParameters().get("method"));
        assertEquals(SID, request.getParameters().get("_sid"));
        // 目的地路径作为 multipart 表单字段，仍要求 JSON 引号包裹。
        assertEquals("\"/downloads\"", request.getParameters().get("destination"));
        assertNull(request.getParameters().get("unzip_password"));
        // 官方要求 file part 放在最后一个表单字段。
        assertEquals(1, request.getMultipartParts().size());
        assertEquals("file", request.getMultipartParts().get(0).getName());
    }

    @Test
    void taskDeleteEncodesIdsAndForceComplete() {
        downloadStation.task().delete(Arrays.asList("dbid_1", "dbid_2"), false);

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task", "3", "delete");
        assertEquals("dbid_1,dbid_2", request.getParameters().get("id"));
        assertEquals("false", request.getParameters().get("force_complete"));

        downloadStation.task().delete("dbid_1");

        SynologyHttpRequest singleRequest = httpClient.getLastRequest();
        assertApi(singleRequest, "SYNO.DownloadStation2.Task", "3", "delete");
        assertEquals("dbid_1", singleRequest.getParameters().get("id"));
        // 单值删除未指定 force_complete 时不下发该参数。
        assertNull(singleRequest.getParameters().get("force_complete"));
    }

    @Test
    void taskPauseAndResumeEncodeCommaSeparatedIds() {
        downloadStation.task().pause(Arrays.asList("dbid_1", "dbid_2"));

        SynologyHttpRequest pauseRequest = httpClient.getLastRequest();
        assertApi(pauseRequest, "SYNO.DownloadStation2.Task", "3", "pause");
        assertEquals("dbid_1,dbid_2", pauseRequest.getParameters().get("id"));

        downloadStation.task().resume("dbid_1");

        SynologyHttpRequest resumeRequest = httpClient.getLastRequest();
        assertApi(resumeRequest, "SYNO.DownloadStation2.Task", "3", "resume");
        assertEquals("dbid_1", resumeRequest.getParameters().get("id"));
    }

    @Test
    void taskEditEncodesIdAndDestination() {
        downloadStation.task().edit("dbid_1", "/downloads/new");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task", "3", "edit");
        assertEquals("dbid_1", request.getParameters().get("id"));
        assertEquals("\"/downloads/new\"", request.getParameters().get("destination"));
    }

    @Test
    void taskFileGetQuotesTaskid() {
        downloadStation.taskFile().get("dbid_1");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task.File", "1", "get");
        // taskid 沿用任务型接口的引号包裹惯例。
        assertEquals("\"dbid_1\"", request.getParameters().get("taskid"));
    }

    @Test
    void statisticGetInfoAndGetStatisticUseStatisticApi() {
        downloadStation.statistic().getInfo();

        assertApi(httpClient.getLastRequest(), "SYNO.DownloadStation2.Statistic", "2", "getInfo");

        downloadStation.statistic().getStatistic();

        assertApi(httpClient.getLastRequest(), "SYNO.DownloadStation2.Statistic", "2", "getStatistic");
    }

    @Test
    void btSearchStartEncodesKeywordAndModule() {
        downloadStation.btSearch().start("ubuntu", "all");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task.BTSearch", "3", "start");
        assertEquals("ubuntu", request.getParameters().get("keyword"));
        assertEquals("all", request.getParameters().get("module"));
    }

    @Test
    void btSearchListEncodesPagingSortAndFilters() {
        downloadStation.btSearch().list(BtSearchListRequest.builder("btsearch_1")
                .offset(0)
                .limit(20)
                .sortBy("size")
                .sortDirection("desc")
                .filterCategory("Software")
                .filterTitle("ubuntu")
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.DownloadStation2.Task.BTSearch", "3", "list");
        assertEquals("\"btsearch_1\"", request.getParameters().get("taskid"));
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("20", request.getParameters().get("limit"));
        assertEquals("size", request.getParameters().get("sort_by"));
        assertEquals("desc", request.getParameters().get("sort_direction"));
        assertEquals("Software", request.getParameters().get("filter_category"));
        assertEquals("ubuntu", request.getParameters().get("filter_title"));
    }

    @Test
    void btSearchCategoryModuleAndCleanUseBtSearchApi() {
        downloadStation.btSearch().getCategory("btsearch_1");

        SynologyHttpRequest categoryRequest = httpClient.getLastRequest();
        assertApi(categoryRequest, "SYNO.DownloadStation2.Task.BTSearch", "3", "getCategory");
        assertEquals("\"btsearch_1\"", categoryRequest.getParameters().get("taskid"));

        downloadStation.btSearch().getModule();

        assertApi(httpClient.getLastRequest(), "SYNO.DownloadStation2.Task.BTSearch", "3", "getModule");

        downloadStation.btSearch().clean("btsearch_1");

        SynologyHttpRequest cleanRequest = httpClient.getLastRequest();
        assertApi(cleanRequest, "SYNO.DownloadStation2.Task.BTSearch", "3", "clean");
        assertEquals("\"btsearch_1\"", cleanRequest.getParameters().get("taskid"));
    }

    @Test
    void rssSiteListAndRefreshEncodePagingAndId() {
        downloadStation.rssSite().list(RssSiteListRequest.builder()
                .offset(0)
                .limit(10)
                .build());

        SynologyHttpRequest listRequest = httpClient.getLastRequest();
        assertApi(listRequest, "SYNO.DownloadStation2.RSS.Site", "1", "list");
        assertEquals("0", listRequest.getParameters().get("offset"));
        assertEquals("10", listRequest.getParameters().get("limit"));

        downloadStation.rssSite().refresh("rss_1");

        SynologyHttpRequest refreshRequest = httpClient.getLastRequest();
        assertApi(refreshRequest, "SYNO.DownloadStation2.RSS.Site", "1", "refresh");
        assertEquals("rss_1", refreshRequest.getParameters().get("id"));
    }

    @Test
    void rssFeedListAndDownloadEncodeFeedParams() {
        downloadStation.rssFeed().list(RssFeedListRequest.builder("rss_1")
                .offset(0)
                .limit(20)
                .build());

        SynologyHttpRequest listRequest = httpClient.getLastRequest();
        assertApi(listRequest, "SYNO.DownloadStation2.RSS.Feed", "1", "list");
        assertEquals("rss_1", listRequest.getParameters().get("id"));
        assertEquals("0", listRequest.getParameters().get("offset"));
        assertEquals("20", listRequest.getParameters().get("limit"));

        downloadStation.rssFeed().download("feed_1");

        SynologyHttpRequest downloadRequest = httpClient.getLastRequest();
        assertApi(downloadRequest, "SYNO.DownloadStation2.RSS.Feed", "1", "download");
        assertEquals("feed_1", downloadRequest.getParameters().get("id"));
    }

    @Test
    void operationResponsesReportSuccess() {
        // fake HTTP client 固定返回 success=true，各写操作应返回成功实体。
        assertTrue(downloadStation.task().delete("dbid_1").isSuccess());
        assertTrue(downloadStation.task().pause("dbid_1").isSuccess());
        assertTrue(downloadStation.task().resume("dbid_1").isSuccess());
        assertTrue(downloadStation.btSearch().clean("btsearch_1").isSuccess());
    }

    /**
     * 断言请求的基础参数：URL、api、version、method 以及认证 SID。
     */
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
