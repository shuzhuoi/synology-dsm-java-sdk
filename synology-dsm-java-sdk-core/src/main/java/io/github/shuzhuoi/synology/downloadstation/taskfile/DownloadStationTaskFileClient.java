package io.github.shuzhuoi.synology.downloadstation.taskfile;

import io.github.shuzhuoi.synology.downloadstation.DownloadStationApi;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.DownloadStation2.Task.File 客户端。
 * <p>
 * 查询某个下载任务内包含的文件列表（例如种子内的多个文件）。
 */
public class DownloadStationTaskFileClient {

    private final SynologyApiExecutor executor;

    public DownloadStationTaskFileClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询任务内的文件列表。
     *
     * @param taskid 任务 ID
     * @return 任务内文件列表
     */
    public TaskFileListResponse get(String taskid) {
        // taskid 沿用 File Station 任务型接口的引号包裹惯例。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_FILE_API, DownloadStationApi.TASK_FILE_VERSION, "get", parameters, TaskFileListResponse.class);
    }
}
