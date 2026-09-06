package io.github.shuzhuoi.synology.downloadstation.rss;

import io.github.shuzhuoi.synology.downloadstation.DownloadStationApi;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.DownloadStation2.RSS.Feed 客户端。
 * <p>
 * 查询某个 RSS 站点下的订阅条目，并把条目转化为下载任务。
 */
public class DownloadStationRssFeedClient {

    private final SynologyApiExecutor executor;

    public DownloadStationRssFeedClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 分页查询指定站点的 RSS 订阅条目。
     *
     * @param request 查询请求
     * @return 条目列表
     */
    public RssFeedListResponse list(RssFeedListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", request.getSiteId());
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.RSS_FEED_API, DownloadStationApi.RSS_FEED_VERSION, "list", parameters, RssFeedListResponse.class);
    }

    /**
     * 把 RSS 条目转化为下载任务。
     *
     * @param id 条目 ID
     * @return 操作结果
     */
    public SynologyOperationResponse download(String id) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", id);
        executor.getAuthenticated("entry.cgi", DownloadStationApi.RSS_FEED_API, DownloadStationApi.RSS_FEED_VERSION, "download", parameters, Object.class);
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
