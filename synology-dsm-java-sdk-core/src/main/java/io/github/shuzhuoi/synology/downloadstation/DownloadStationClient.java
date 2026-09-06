package io.github.shuzhuoi.synology.downloadstation;

import io.github.shuzhuoi.synology.downloadstation.btsearch.DownloadStationBtSearchClient;
import io.github.shuzhuoi.synology.downloadstation.info.DownloadStationInfoClient;
import io.github.shuzhuoi.synology.downloadstation.rss.DownloadStationRssFeedClient;
import io.github.shuzhuoi.synology.downloadstation.rss.DownloadStationRssSiteClient;
import io.github.shuzhuoi.synology.downloadstation.statistic.DownloadStationStatisticClient;
import io.github.shuzhuoi.synology.downloadstation.task.DownloadStationTaskClient;
import io.github.shuzhuoi.synology.downloadstation.taskfile.DownloadStationTaskFileClient;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

/**
 * Download Station API 聚合入口。
 * <p>
 * 该类不直接发请求，只负责把 Download Station 的能力按资源类型拆分到不同客户端中。
 */
public class DownloadStationClient {

    /**
     * Download Station 基础信息与全局配置接口。
     */
    private final DownloadStationInfoClient infoClient;
    /**
     * 下载任务增删改查、暂停恢复接口。
     */
    private final DownloadStationTaskClient taskClient;
    /**
     * 任务内文件列表接口。
     */
    private final DownloadStationTaskFileClient taskFileClient;
    /**
     * 速度与累计流量统计接口。
     */
    private final DownloadStationStatisticClient statisticClient;
    /**
     * BT 种子搜索接口。
     */
    private final DownloadStationBtSearchClient btSearchClient;
    /**
     * RSS 订阅站点接口。
     */
    private final DownloadStationRssSiteClient rssSiteClient;
    /**
     * RSS 订阅条目接口。
     */
    private final DownloadStationRssFeedClient rssFeedClient;

    public DownloadStationClient(SynologyApiExecutor executor) {
        this.infoClient = new DownloadStationInfoClient(executor);
        this.taskClient = new DownloadStationTaskClient(executor);
        this.taskFileClient = new DownloadStationTaskFileClient(executor);
        this.statisticClient = new DownloadStationStatisticClient(executor);
        this.btSearchClient = new DownloadStationBtSearchClient(executor);
        this.rssSiteClient = new DownloadStationRssSiteClient(executor);
        this.rssFeedClient = new DownloadStationRssFeedClient(executor);
    }

    public DownloadStationInfoClient info() {
        return infoClient;
    }

    public DownloadStationTaskClient task() {
        return taskClient;
    }

    public DownloadStationTaskFileClient taskFile() {
        return taskFileClient;
    }

    public DownloadStationStatisticClient statistic() {
        return statisticClient;
    }

    public DownloadStationBtSearchClient btSearch() {
        return btSearchClient;
    }

    public DownloadStationRssSiteClient rssSite() {
        return rssSiteClient;
    }

    public DownloadStationRssFeedClient rssFeed() {
        return rssFeedClient;
    }
}
