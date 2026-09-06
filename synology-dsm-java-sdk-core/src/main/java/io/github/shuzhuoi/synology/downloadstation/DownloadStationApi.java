package io.github.shuzhuoi.synology.downloadstation;

/**
 * Download Station API 名称与版本常量。
 * <p>
 * DSM 7 的 Download Station 使用 SYNO.DownloadStation2.* 系列接口，统一走 entry.cgi。
 * 注意：以下版本号依据 DSM 7 社区文档推定，请以
 * {@code DownloadStationCoverageExample} 启动时打印的 SYNO.API.Info 探测结果为准，
 * 如有偏差只需修正本类中的常量，无需改动各客户端。
 */
public final class DownloadStationApi {

    /**
     * Download Station 信息与全局配置 API。
     */
    public static final String INFO_API = "SYNO.DownloadStation2.Info";
    /**
     * Info API 版本。
     */
    public static final int INFO_VERSION = 3;

    /**
     * 下载任务管理 API（增删改查、暂停恢复）。
     */
    public static final String TASK_API = "SYNO.DownloadStation2.Task";
    /**
     * Task API 版本。
     */
    public static final int TASK_VERSION = 3;

    /**
     * 任务内文件列表 API。
     */
    public static final String TASK_FILE_API = "SYNO.DownloadStation2.Task.File";
    /**
     * Task.File API 版本。
     */
    public static final int TASK_FILE_VERSION = 1;

    /**
     * 下载速度与累计流量统计 API。
     */
    public static final String STATISTIC_API = "SYNO.DownloadStation2.Statistic";
    /**
     * Statistic API 版本。
     */
    public static final int STATISTIC_VERSION = 2;

    /**
     * BT 种子搜索引擎 API。
     */
    public static final String BT_SEARCH_API = "SYNO.DownloadStation2.Task.BTSearch";
    /**
     * BTSearch API 版本。
     */
    public static final int BT_SEARCH_VERSION = 3;

    /**
     * RSS 订阅站点管理 API。
     */
    public static final String RSS_SITE_API = "SYNO.DownloadStation2.RSS.Site";
    /**
     * RSS.Site API 版本。
     */
    public static final int RSS_SITE_VERSION = 1;

    /**
     * RSS 订阅条目 API。
     */
    public static final String RSS_FEED_API = "SYNO.DownloadStation2.RSS.Feed";
    /**
     * RSS.Feed API 版本。
     */
    public static final int RSS_FEED_VERSION = 1;

    private DownloadStationApi() {
    }
}
