package io.github.shuzhuoi.synology.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.downloadstation.btsearch.BtSearchCategoryListResponse;
import io.github.shuzhuoi.synology.downloadstation.btsearch.BtSearchItem;
import io.github.shuzhuoi.synology.downloadstation.btsearch.BtSearchListRequest;
import io.github.shuzhuoi.synology.downloadstation.btsearch.BtSearchListResponse;
import io.github.shuzhuoi.synology.downloadstation.btsearch.BtSearchModuleListResponse;
import io.github.shuzhuoi.synology.downloadstation.btsearch.BtSearchStartResponse;
import io.github.shuzhuoi.synology.downloadstation.info.DownloadStationConfigResponse;
import io.github.shuzhuoi.synology.downloadstation.info.DownloadStationInfoResponse;
import io.github.shuzhuoi.synology.downloadstation.model.DownloadTask;
import io.github.shuzhuoi.synology.downloadstation.model.TaskAdditionalField;
import io.github.shuzhuoi.synology.downloadstation.rss.RssFeed;
import io.github.shuzhuoi.synology.downloadstation.rss.RssFeedListRequest;
import io.github.shuzhuoi.synology.downloadstation.rss.RssFeedListResponse;
import io.github.shuzhuoi.synology.downloadstation.rss.RssSite;
import io.github.shuzhuoi.synology.downloadstation.rss.RssSiteListRequest;
import io.github.shuzhuoi.synology.downloadstation.rss.RssSiteListResponse;
import io.github.shuzhuoi.synology.downloadstation.statistic.StatisticInfoResponse;
import io.github.shuzhuoi.synology.downloadstation.statistic.StatisticSummaryResponse;
import io.github.shuzhuoi.synology.downloadstation.task.TaskCreateRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskCreateTorrentRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskGetInfoRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskGetInfoResponse;
import io.github.shuzhuoi.synology.downloadstation.task.TaskListRequest;
import io.github.shuzhuoi.synology.downloadstation.task.TaskListResponse;
import io.github.shuzhuoi.synology.downloadstation.taskfile.TaskFileListResponse;
import io.github.shuzhuoi.synology.example.config.DownloadStationCoverageExampleConfig;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Download Station 官方契约覆盖示例。
 * <p>
 * 演示 DSM 7 新契约 SYNO.DownloadStation2.* 的全部能力：
 * Info（版本与全局配置）、Statistic（速度与累计流量）、Task（URI/种子创建、列表、详情、暂停恢复、删除）、
 * Task.File（任务内文件）、BTSearch（搜索引擎、搜索、结果、清理）、RSS.Site/Feed（站点与订阅条目）。
 * <p>
 * 运行前请复制 classpath 下的 downloadstation-coverage.example.yaml 为
 * downloadstation-coverage.yaml，并填写真实 DSM 地址、账号、密码和下载目的地。
 * testUri / torrentFile / btSearchKeyword 为可选演示段，留空时自动跳过；
 * 示例创建的下载任务会在演示结束后删除，不影响 DSM 上已有的任务。
 */
@Slf4j
public class DownloadStationCoverageExample {

    private static final String CONFIG_FILE = "downloadstation-coverage.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "downloadstation-coverage.example.yaml";
    /**
     * 创建任务后等待任务出现在列表中的最大轮询次数。
     */
    private static final int TASK_APPEAR_MAX_ATTEMPTS = 10;
    /**
     * BT 搜索等待搜索引擎返回全部结果的最大轮询次数。
     */
    private static final int BT_SEARCH_MAX_ATTEMPTS = 15;

    public static void main(String[] args) throws IOException {
        DownloadStationCoverageExampleConfig sampleConfig = readSampleConfig();
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(requiredConfigValue(sampleConfig.getDsmUrl(), "dsmUrl"))
                .account(requiredConfigValue(sampleConfig.getAccount(), "account"))
                .password(requiredConfigValue(sampleConfig.getPassword(), "password"))
                .autoRefreshSession(Boolean.TRUE)
                .build();

        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(config, new JacksonSynologyJsonCodec());
        String destination = requiredConfigValue(sampleConfig.getDestination(), "destination");

        try {
            // 触发登录并复用 SID。
            client.session().currentSession();
            showInfo(client);
            showStatistic(client);
            showTaskList(client);

            if (isConfigured(sampleConfig.getTestUri())) {
                createAndManageUriTask(client, sampleConfig.getTestUri(), destination);
            } else {
                log.info("testUri 未配置，跳过 URI 任务创建段演示");
            }

            if (isConfigured(sampleConfig.getTorrentFile())) {
                File torrentFile = new File(sampleConfig.getTorrentFile());
                if (!torrentFile.isFile()) {
                    log.warn("torrentFile 配置的本地文件不存在：{}，跳过种子任务创建段演示", sampleConfig.getTorrentFile());
                } else {
                    createAndCleanupTorrentTask(client, torrentFile, destination);
                }
            } else {
                log.info("torrentFile 未配置，跳过种子任务创建段演示");
            }

            if (isConfigured(sampleConfig.getBtSearchKeyword())) {
                btSearchDemo(client, sampleConfig.getBtSearchKeyword());
            } else {
                log.info("btSearchKeyword 未配置，跳过 BT 搜索段演示");
            }

            rssDemo(client);
            log.info("Download Station 官方覆盖示例执行完成");
        } finally {
            client.session().logout();
        }
    }

    /**
     * 演示 Info：getInfo 返回版本信息，getConfig 返回全局配置（只读演示，不调用 setServerConfig 修改）。
     */
    private static void showInfo(SynologyDsmClient client) {
        DownloadStationInfoResponse info = client.downloadStation().info().getInfo();
        log.info("Download Station 版本：{}（versionString={}，当前账号是否管理员={}）",
                info.getVersion(), info.getVersionString(), info.getManager());

        DownloadStationConfigResponse config = client.downloadStation().info().getConfig();
        log.info("全局配置：BT 限速下载={}KB/s 上传={}KB/s，默认目的地={}，eMule 启用={}，自动解压={}",
                config.getBtMaxDownload(), config.getBtMaxUpload(),
                config.getDefaultDestination(), config.getEmuleEnabled(), config.getUnzipServiceEnabled());
    }

    /**
     * 演示 Statistic：getInfo 返回实时速度，getStatistic 返回累计流量。
     */
    private static void showStatistic(SynologyDsmClient client) {
        StatisticInfoResponse info = client.downloadStation().statistic().getInfo();
        log.info("当前速度：下载={}B/s，上传={}B/s", info.getSpeedDownload(), info.getSpeedUpload());

        StatisticSummaryResponse summary = client.downloadStation().statistic().getStatistic();
        log.info("累计流量：下载={}B，上传={}B", summary.getSizeDownload(), summary.getSizeUpload());
    }

    /**
     * 演示 Task list：分页查询任务并携带 detail / transfer 扩展块。
     */
    private static void showTaskList(SynologyDsmClient client) {
        TaskListResponse response = client.downloadStation().task().list(
                TaskListRequest.builder()
                        .limit(10)
                        .addAdditional(TaskAdditionalField.DETAIL)
                        .addAdditional(TaskAdditionalField.TRANSFER)
                        .build()
        );
        log.info("当前任务总数={}（本次最多展示 10 条）", response.getTotal());
        List<DownloadTask> tasks = response.getTasks();
        if (tasks == null) {
            return;
        }
        for (DownloadTask task : tasks) {
            log.info("任务：id={}，标题={}，类型={}，状态={}，大小={}B", task.getId(), task.getTitle(),
                    task.getType(), task.getStatus(), task.getSize());
        }
    }

    /**
     * 演示 Task create（URI 形态）以及 getinfo / Task.File get / pause / resume / delete 全生命周期。
     * <p>
     * 通过创建前后任务 ID 差集定位新任务，演示结束后删除该任务。
     */
    private static void createAndManageUriTask(SynologyDsmClient client, String testUri, String destination) {
        Set<String> taskIdsBefore = collectTaskIds(client);
        client.downloadStation().task().create(
                TaskCreateRequest.builder(testUri)
                        .destination(destination)
                        .createList(Boolean.FALSE)
                        .build()
        );
        log.info("已通过 URI 创建下载任务：{}，目的地={}", testUri, destination);

        String taskId = waitNewTaskId(client, taskIdsBefore);
        if (taskId == null) {
            log.warn("未能定位新建任务，跳过后续生命周期演示（该任务可能仍在 DSM 队列中，请手动检查）");
            return;
        }
        try {
            TaskGetInfoResponse info = client.downloadStation().task().getInfo(
                    TaskGetInfoRequest.builder(taskId)
                            .addAdditional(TaskAdditionalField.DETAIL)
                            .addAdditional(TaskAdditionalField.TRANSFER)
                            .build()
            );
            List<DownloadTask> tasks = info.getTasks();
            if (tasks != null && !tasks.isEmpty()) {
                DownloadTask task = tasks.get(0);
                log.info("任务详情：id={}，标题={}，状态={}，目的地={}，已下载={}B",
                        task.getId(), task.getTitle(), task.getStatus(),
                        task.getAdditional() != null && task.getAdditional().getDetail() != null
                                ? task.getAdditional().getDetail().getDestination() : null,
                        task.getAdditional() != null && task.getAdditional().getTransfer() != null
                                ? task.getAdditional().getTransfer().getSizeDownloaded() : null);
            }

            TaskFileListResponse files = client.downloadStation().taskFile().get(taskId);
            log.info("任务内文件数={}（total={}）",
                    files.getFiles() == null ? 0 : files.getFiles().size(), files.getTotal());

            client.downloadStation().task().pause(taskId);
            log.info("任务已暂停：{}", taskId);
            client.downloadStation().task().resume(taskId);
            log.info("任务已恢复：{}", taskId);
        } finally {
            // 演示结束删除任务，未完成任务直接丢弃已下载数据。
            client.downloadStation().task().delete(taskId);
            log.info("任务已删除：{}", taskId);
        }
    }

    /**
     * 演示 Task create（.torrent 文件上传形态），演示结束后删除该任务。
     */
    private static void createAndCleanupTorrentTask(SynologyDsmClient client, File torrentFile, String destination) {
        Set<String> taskIdsBefore = collectTaskIds(client);
        client.downloadStation().task().createTorrent(
                TaskCreateTorrentRequest.builder(torrentFile)
                        .destination(destination)
                        .createList(Boolean.FALSE)
                        .build()
        );
        log.info("已通过种子文件创建下载任务：{}，目的地={}", torrentFile.getName(), destination);

        String taskId = waitNewTaskId(client, taskIdsBefore);
        if (taskId == null) {
            log.warn("未能定位新建种子任务，请手动检查 DSM 下载队列");
            return;
        }
        client.downloadStation().task().delete(taskId);
        log.info("种子任务已删除：{}", taskId);
    }

    /**
     * 演示 BTSearch：getModule 查询搜索引擎、start 发起搜索、轮询 list、getCategory 查询分类、clean 清理。
     */
    private static void btSearchDemo(SynologyDsmClient client, String keyword) {
        BtSearchModuleListResponse modules = client.downloadStation().btSearch().getModule();
        log.info("已启用的搜索引擎模块数={}", modules.getModules() == null ? 0 : modules.getModules().size());

        BtSearchStartResponse start = client.downloadStation().btSearch().start(keyword, null);
        String taskid = start.getTaskid();
        log.info("BT 搜索任务已启动：keyword={}，taskid={}", keyword, taskid);
        try {
            BtSearchListResponse response = waitBtSearchFinished(client, taskid);
            log.info("BT 搜索结果：total={}，本次返回={} 条",
                    response.getTotal(), response.getItems() == null ? 0 : response.getItems().size());
            if (response.getItems() != null) {
                for (int i = 0; i < Math.min(3, response.getItems().size()); i++) {
                    BtSearchItem item = response.getItems().get(i);
                    log.info("搜索结果[{}]：标题={}，大小={}B，peers={}，seeds={}",
                            i + 1, item.getTitle(), item.getSize(), item.getPeers(), item.getSeeds());
                }
            }

            BtSearchCategoryListResponse categories = client.downloadStation().btSearch().getCategory(taskid);
            log.info("搜索结果分类数={}",
                    categories.getCategories() == null ? 0 : categories.getCategories().size());
        } finally {
            client.downloadStation().btSearch().clean(taskid);
            log.info("BT 搜索任务已清理：{}", taskid);
        }
    }

    /**
     * 演示 RSS.Site list / refresh 与 RSS.Feed list。
     * <p>
     * Feed download 会创建真实下载任务，示例不做演示，仅在注释中说明入口。
     */
    private static void rssDemo(SynologyDsmClient client) {
        RssSiteListResponse sites = client.downloadStation().rssSite().list(
                RssSiteListRequest.builder()
                        .limit(-1)
                        .build()
        );
        log.info("RSS 订阅站点数={}", sites.getSites() == null ? 0 : sites.getSites().size());
        List<RssSite> siteList = sites.getSites();
        if (siteList == null || siteList.isEmpty()) {
            log.info("DSM 上暂无 RSS 订阅站点，跳过 RSS 演示");
            return;
        }

        RssSite firstSite = siteList.get(0);
        client.downloadStation().rssSite().refresh(firstSite.getId());
        log.info("已刷新第一个 RSS 站点：id={}，标题={}", firstSite.getId(), firstSite.getTitle());

        RssFeedListResponse feeds = client.downloadStation().rssFeed().list(
                RssFeedListRequest.builder(firstSite.getId())
                        .limit(5)
                        .build()
        );
        log.info("RSS 站点条目总数={}（本次最多展示 5 条）", feeds.getTotal());
        if (feeds.getFeeds() != null) {
            for (RssFeed feed : feeds.getFeeds()) {
                log.info("RSS 条目：id={}，标题={}，可下载链接={}", feed.getId(), feed.getTitle(), feed.getDownloadUri());
            }
        }
        // 如需把某个条目转化为下载任务，可调用：
        // client.downloadStation().rssFeed().download(feed.getId());
    }

    /**
     * 收集当前 DSM 上的全部任务 ID，用于创建前后差集定位新任务。
     */
    private static Set<String> collectTaskIds(SynologyDsmClient client) {
        TaskListResponse response = client.downloadStation().task().list(TaskListRequest.builder().limit(-1).build());
        Set<String> ids = new HashSet<String>();
        if (response.getTasks() != null) {
            for (DownloadTask task : response.getTasks()) {
                ids.add(task.getId());
            }
        }
        return ids;
    }

    /**
     * 轮询任务列表，返回不在差集之前集合中的新任务 ID。
     *
     * @return 新任务 ID，超时未出现时返回 null
     */
    private static String waitNewTaskId(SynologyDsmClient client, Set<String> taskIdsBefore) {
        for (int i = 0; i < TASK_APPEAR_MAX_ATTEMPTS; i++) {
            TaskListResponse response = client.downloadStation().task().list(
                    TaskListRequest.builder().limit(-1).build()
            );
            if (response.getTasks() != null) {
                for (DownloadTask task : response.getTasks()) {
                    if (!taskIdsBefore.contains(task.getId())) {
                        return task.getId();
                    }
                }
            }
            sleepQuiet(1000L);
        }
        return null;
    }

    /**
     * 轮询 BT 搜索结果，直到搜索引擎标记 finished 或超过最大尝试次数。
     */
    private static BtSearchListResponse waitBtSearchFinished(SynologyDsmClient client, String taskid) {
        BtSearchListResponse response = client.downloadStation().btSearch().list(
                BtSearchListRequest.builder(taskid).limit(5).build()
        );
        for (int i = 0; i < BT_SEARCH_MAX_ATTEMPTS; i++) {
            if (Boolean.TRUE.equals(response.getFinished())) {
                return response;
            }
            sleepQuiet(1000L);
            response = client.downloadStation().btSearch().list(
                    BtSearchListRequest.builder(taskid).limit(5).build()
            );
        }
        log.warn("BT 搜索在 {} 次轮询后仍未结束，按最后一次结果继续演示", BT_SEARCH_MAX_ATTEMPTS);
        return response;
    }

    private static DownloadStationCoverageExampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = DownloadStationCoverageExample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, DownloadStationCoverageExampleConfig.class);
        }
    }

    private static String requiredConfigValue(String value, String fieldName) {
        if (!isConfigured(value)) {
            throw new IllegalArgumentException("示例配置文件 " + CONFIG_FILE + " 缺少必填配置：" + fieldName);
        }
        return value;
    }

    /**
     * 判断可选配置是否有效填写（非 null 且非空白）。
     */
    private static boolean isConfigured(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static void sleepQuiet(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
