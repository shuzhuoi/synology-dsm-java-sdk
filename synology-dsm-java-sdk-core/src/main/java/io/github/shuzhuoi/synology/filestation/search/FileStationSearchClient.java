package io.github.shuzhuoi.synology.filestation.search;

import io.github.shuzhuoi.synology.filestation.task.SynologyTaskPoller;
import io.github.shuzhuoi.synology.filestation.task.TaskPollingOptions;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.task.TaskStopResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.Search 客户端。
 * <p>
 * 按条件搜索文件，属于非阻塞任务型 API：start 返回 taskid，
 * list 轮询获取匹配结果，stop 取消搜索但不清临时数据库，clean 删除临时数据库。
 * 完成后必须调用 clean 释放 DSM 端资源，否则临时数据库会一直占用。
 */
public class FileStationSearchClient {

    private final SynologyApiExecutor executor;

    public FileStationSearchClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public TaskStartResponse start(SearchStartRequest request) {
        // folder_path 支持多目录，编码为 JSON-like 数组字符串。多条件之间为 AND 关系。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("folder_path", SynologyParameterEncoder.stringList(request.getFolderPaths()));
        parameters.put("recursive", SynologyParameterEncoder.booleanValue(request.getRecursive()));
        parameters.put("pattern", request.getPattern());
        parameters.put("extension", request.getExtension());
        parameters.put("filetype", request.getFiletype());
        parameters.put("size_from", SynologyParameterEncoder.longValue(request.getSizeFrom()));
        parameters.put("size_to", SynologyParameterEncoder.longValue(request.getSizeTo()));
        parameters.put("mtime_from", SynologyParameterEncoder.longValue(request.getMtimeFrom()));
        parameters.put("mtime_to", SynologyParameterEncoder.longValue(request.getMtimeTo()));
        parameters.put("crtime_from", SynologyParameterEncoder.longValue(request.getCrtimeFrom()));
        parameters.put("crtime_to", SynologyParameterEncoder.longValue(request.getCrtimeTo()));
        parameters.put("atime_from", SynologyParameterEncoder.longValue(request.getAtimeFrom()));
        parameters.put("atime_to", SynologyParameterEncoder.longValue(request.getAtimeTo()));
        parameters.put("owner", request.getOwner());
        parameters.put("group", request.getGroup());
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.Search", 2, "start", parameters, TaskStartResponse.class);
    }

    public SearchListResponse list(SearchListRequest request) {
        // list 返回匹配文件列表和 finished 标记，调用方可据此判断搜索是否仍在进行。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(request.getTaskid()));
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("pattern", request.getPattern());
        parameters.put("filetype", request.getFiletype());
        parameters.put("additional", SynologyParameterEncoder.additionalList(request.getAdditional()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.Search", 2, "list", parameters, SearchListResponse.class);
    }

    public TaskStopResponse stop(String taskid) {
        // stop 仅取消搜索，临时数据库仍保留，可继续 list；完成后需调用 clean 清理。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        executor.getAuthenticated("entry.cgi", "SYNO.FileStation.Search", 2, "stop", parameters, Object.class);
        return new TaskStopResponse(true);
    }

    public TaskStopResponse clean(String taskid) {
        // clean 删除搜索临时数据库，释放 DSM 端资源。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        executor.getAuthenticated("entry.cgi", "SYNO.FileStation.Search", 2, "clean", parameters, Object.class);
        return new TaskStopResponse(true);
    }

    /**
     * 轮询 list 直到搜索完成，返回最终的匹配结果。
     * <p>
     * 注意：该方法不会自动 clean 临时数据库，调用方完成后应自行调用 clean。
     *
     * @param taskid  start 返回的任务 ID
     * @param options 轮询配置
     * @return 搜索完成后的列表响应
     */
    public SearchListResponse wait(String taskid, TaskPollingOptions options) {
        SearchListRequest request = SearchListRequest.builder(taskid).build();
        return SynologyTaskPoller.wait(
                () -> list(request),
                response -> Boolean.TRUE.equals(response.getFinished()),
                () -> stop(taskid),
                options
        );
    }
}
