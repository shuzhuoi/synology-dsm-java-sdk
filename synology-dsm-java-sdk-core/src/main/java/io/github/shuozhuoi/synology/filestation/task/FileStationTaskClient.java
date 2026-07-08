package io.github.shuzhuoi.synology.filestation.task;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * File Station 任务型接口客户端。
 * <p>
 * v0.1 先实现 MD5 任务，后续 Search、DirSize、Compress、Extract 可复用类似结构。
 */
public class FileStationTaskClient {

    private final SynologyApiExecutor executor;

    public FileStationTaskClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public TaskStartResponse md5(Md5Request request) {
        // MD5 是异步任务，start 返回 taskid，需要继续调用 status 获取结果。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("file_path", SynologyParameterEncoder.quoted(request.getFilePath()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.MD5", 2, "start", parameters, TaskStartResponse.class);
    }

    public Md5StatusResponse md5Status(String taskId) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskId));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.MD5", 2, "status", parameters, Md5StatusResponse.class);
    }

    public TaskStopResponse md5Stop(String taskId) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskId));
        executor.getAuthenticated("entry.cgi", "SYNO.FileStation.MD5", 2, "stop", parameters, Object.class);
        return new TaskStopResponse(true);
    }
}
