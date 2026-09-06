package io.github.shuzhuoi.synology.downloadstation.statistic;

import io.github.shuzhuoi.synology.downloadstation.DownloadStationApi;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

import java.util.Collections;

/**
 * SYNO.DownloadStation2.Statistic 客户端。
 * <p>
 * 提供当前速度与累计流量两类统计。
 */
public class DownloadStationStatisticClient {

    private final SynologyApiExecutor executor;

    public DownloadStationStatisticClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public StatisticInfoResponse getInfo() {
        // getInfo 返回各引擎的实时上下行速度。
        return executor.getAuthenticated(
                "entry.cgi",
                DownloadStationApi.STATISTIC_API,
                DownloadStationApi.STATISTIC_VERSION,
                "getInfo",
                Collections.<String, String>emptyMap(),
                StatisticInfoResponse.class
        );
    }

    public StatisticSummaryResponse getStatistic() {
        // getStatistic 返回各引擎的历史累计上下行流量。
        return executor.getAuthenticated(
                "entry.cgi",
                DownloadStationApi.STATISTIC_API,
                DownloadStationApi.STATISTIC_VERSION,
                "getStatistic",
                Collections.<String, String>emptyMap(),
                StatisticSummaryResponse.class
        );
    }
}
