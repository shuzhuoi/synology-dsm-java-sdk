package io.github.shuzhuoi.synology.downloadstation.btsearch;

import io.github.shuzhuoi.synology.downloadstation.DownloadStationApi;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.DownloadStation2.Task.BTSearch 客户端。
 * <p>
 * 使用 DSM 内置的 BT 搜索引擎检索种子。
 * 注意：需要在 Download Station 设置中启用至少一个搜索引擎才能搜索。
 */
public class DownloadStationBtSearchClient {

    private final SynologyApiExecutor executor;

    public DownloadStationBtSearchClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 发起关键字搜索。
     *
     * @param keyword 搜索关键字
     * @param module  搜索引擎模块名，null 时使用 all
     * @return 包含搜索任务 ID 的响应
     */
    public BtSearchStartResponse start(String keyword, String module) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("keyword", keyword);
        parameters.put("module", module);
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.BT_SEARCH_API, DownloadStationApi.BT_SEARCH_VERSION, "start", parameters, BtSearchStartResponse.class);
    }

    /**
     * 查询搜索结果。
     *
     * @param request 查询请求
     * @return 搜索结果条目
     */
    public BtSearchListResponse list(BtSearchListRequest request) {
        // taskid 沿用 File Station 任务型接口的引号包裹惯例。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(request.getTaskid()));
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("filter_category", request.getFilterCategory());
        parameters.put("filter_title", request.getFilterTitle());
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.BT_SEARCH_API, DownloadStationApi.BT_SEARCH_VERSION, "list", parameters, BtSearchListResponse.class);
    }

    /**
     * 查询搜索任务可用分类。
     *
     * @param taskid 搜索任务 ID
     * @return 分类列表
     */
    public BtSearchCategoryListResponse getCategory(String taskid) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.BT_SEARCH_API, DownloadStationApi.BT_SEARCH_VERSION, "getCategory", parameters, BtSearchCategoryListResponse.class);
    }

    /**
     * 查询已启用的搜索引擎模块。
     *
     * @return 模块列表
     */
    public BtSearchModuleListResponse getModule() {
        // getModule 无业务参数。
        return executor.getAuthenticated(
                "entry.cgi",
                DownloadStationApi.BT_SEARCH_API,
                DownloadStationApi.BT_SEARCH_VERSION,
                "getModule",
                Collections.<String, String>emptyMap(),
                BtSearchModuleListResponse.class
        );
    }

    /**
     * 清理搜索任务及其结果。
     *
     * @param taskid 搜索任务 ID
     * @return 操作结果
     */
    public SynologyOperationResponse clean(String taskid) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        executor.getAuthenticated("entry.cgi", DownloadStationApi.BT_SEARCH_API, DownloadStationApi.BT_SEARCH_VERSION, "clean", parameters, Object.class);
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
