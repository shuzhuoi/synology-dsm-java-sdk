package io.github.shuzhuoi.synology.filestation.backgroundtask;

import io.github.shuzhuoi.synology.filestation.task.TaskStopResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.BackgroundTask 客户端。
 * <p>
 * 列出 copy、move、delete、compress、extract 等非阻塞后台任务，
 * 并提供清理已完成任务的能力。具体任务的进度查询和取消仍在各自的 API 客户端中完成
 * （例如 CopyMove 的 status/stop 在 FileStationFileClient）。
 */
public class FileStationBackgroundTaskClient {

    private final SynologyApiExecutor executor;

    public FileStationBackgroundTaskClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public BackgroundTaskListResponse list(BackgroundTaskListRequest request) {
        // api_filter 支持多个 API 名称，编码为 JSON-like 数组字符串。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("api_filter", SynologyParameterEncoder.stringList(request.getApiFilter()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.BackgroundTask", 3, "list", parameters, BackgroundTaskListResponse.class);
    }

    /**
     * 清空所有已完成的后台任务。
     */
    public TaskStopResponse clearFinished() {
        // 不传 taskid 时，官方会清空所有已完成任务。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        executor.getAuthenticated("entry.cgi", "SYNO.FileStation.BackgroundTask", 3, "clear_finished", parameters, Object.class);
        return new TaskStopResponse(true);
    }

    /**
     * 清理指定的已完成后台任务。
     *
     * @param taskids 要清理的任务 ID 列表
     */
    public TaskStopResponse clearFinished(java.util.List<String> taskids) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.stringList(taskids));
        executor.getAuthenticated("entry.cgi", "SYNO.FileStation.BackgroundTask", 3, "clear_finished", parameters, Object.class);
        return new TaskStopResponse(true);
    }
}
