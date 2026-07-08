package io.github.shuzhuoi.synology.filestation.dirsize;

import io.github.shuzhuoi.synology.filestation.task.SynologyTaskPoller;
import io.github.shuzhuoi.synology.filestation.task.TaskPollingOptions;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.task.TaskStopResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.DirSize 客户端。
 * <p>
 * 计算文件/文件夹累计大小，属于非阻塞任务型 API：start 返回 taskid，
 * status 轮询进度，stop 取消计算。
 */
public class FileStationDirSizeClient {

    private final SynologyApiExecutor executor;

    public FileStationDirSizeClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public TaskStartResponse start(DirSizeStartRequest request) {
        // path 支持多个路径，编码为 JSON-like 数组字符串。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.DirSize", 2, "start", parameters, TaskStartResponse.class);
    }

    public DirSizeStatusResponse status(String taskid) {
        // status 返回 finished 标记和计算结果（目录数、文件数、累计大小）。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.DirSize", 2, "status", parameters, DirSizeStatusResponse.class);
    }

    public TaskStopResponse stop(String taskid) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        executor.getAuthenticated("entry.cgi", "SYNO.FileStation.DirSize", 2, "stop", parameters, Object.class);
        return new TaskStopResponse(true);
    }

    /**
     * 轮询 status 直到计算完成，返回最终的大小统计结果。
     *
     * @param taskid  start 返回的任务 ID
     * @param options 轮询配置
     * @return 计算完成后的状态响应
     */
    public DirSizeStatusResponse wait(String taskid, TaskPollingOptions options) {
        return SynologyTaskPoller.wait(
                () -> status(taskid),
                response -> Boolean.TRUE.equals(response.getFinished()),
                options
        );
    }
}
