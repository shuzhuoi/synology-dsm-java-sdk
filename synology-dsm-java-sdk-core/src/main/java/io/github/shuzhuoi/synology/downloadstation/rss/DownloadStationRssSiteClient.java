package io.github.shuzhuoi.synology.downloadstation.rss;

import io.github.shuzhuoi.synology.downloadstation.DownloadStationApi;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.DownloadStation2.RSS.Site 客户端。
 * <p>
 * 管理 Download Station 中已配置的 RSS 订阅站点。
 */
public class DownloadStationRssSiteClient {

    private final SynologyApiExecutor executor;

    public DownloadStationRssSiteClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 分页查询 RSS 订阅站点列表。
     *
     * @param request 查询请求
     * @return 站点列表
     */
    public RssSiteListResponse list(RssSiteListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.RSS_SITE_API, DownloadStationApi.RSS_SITE_VERSION, "list", parameters, RssSiteListResponse.class);
    }

    /**
     * 立即刷新指定站点的 RSS 内容。
     *
     * @param id 站点 ID
     * @return 操作结果
     */
    public SynologyOperationResponse refresh(String id) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", id);
        executor.getAuthenticated("entry.cgi", DownloadStationApi.RSS_SITE_API, DownloadStationApi.RSS_SITE_VERSION, "refresh", parameters, Object.class);
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
