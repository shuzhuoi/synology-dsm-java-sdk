package io.github.shuzhuoi.synology.filestation.compress;

import io.github.shuzhuoi.synology.filestation.task.SynologyTaskPoller;
import io.github.shuzhuoi.synology.filestation.task.TaskPollingOptions;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.task.TaskStopResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.Compress 客户端。
 * <p>
 * 压缩属于非阻塞任务型 API：start 返回 taskid，status 查询进度，stop 取消任务。
 */
public class FileStationCompressClient {

    private static final String API_NAME = "SYNO.FileStation.Compress";
    private static final int API_VERSION = 3;

    private final SynologyApiExecutor executor;

    public FileStationCompressClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public TaskStartResponse start(CompressStartRequest request) {
        // path 支持多个文件/目录，必须编码为官方要求的 JSON-like 数组字符串。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        parameters.put("dest_file_path", SynologyParameterEncoder.quoted(request.getDestFilePath()));
        parameters.put("level", request.getLevel());
        parameters.put("mode", request.getMode());
        parameters.put("format", request.getFormat());
        parameters.put("password", request.getPassword());
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "start", parameters, TaskStartResponse.class);
    }

    public CompressStatusResponse status(String taskid) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "status", parameters, CompressStatusResponse.class);
    }

    public TaskStopResponse stop(String taskid) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "stop", parameters, Object.class);
        return new TaskStopResponse(true);
    }

    /**
     * 轮询 status 直到压缩任务完成。
     *
     * @param taskid  start 返回的任务 ID
     * @param options 轮询配置
     * @return 压缩完成后的状态响应
     */
    public CompressStatusResponse wait(String taskid, TaskPollingOptions options) {
        return SynologyTaskPoller.wait(
                () -> status(taskid),
                response -> Boolean.TRUE.equals(response.getFinished()),
                () -> stop(taskid),
                options
        );
    }
}
