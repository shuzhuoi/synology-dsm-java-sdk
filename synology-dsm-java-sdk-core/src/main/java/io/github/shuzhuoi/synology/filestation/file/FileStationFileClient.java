package io.github.shuzhuoi.synology.filestation.file;

import io.github.shuzhuoi.synology.filestation.task.SynologyTaskPoller;
import io.github.shuzhuoi.synology.filestation.task.TaskPollingOptions;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.task.TaskStatusResponse;
import io.github.shuzhuoi.synology.filestation.task.TaskStopResponse;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * File Station 文件操作客户端。
 * <p>
 * 包含创建目录、重命名、阻塞/非阻塞删除、复制和移动。
 */
public class FileStationFileClient {

    /**
     * 同步删除默认轮询配置：间隔 500ms，最多 120 次（约 1 分钟）。
     */
    private static final TaskPollingOptions DELETE_WAIT_OPTIONS = TaskPollingOptions.builder()
            .intervalMillis(500L)
            .maxAttempts(120)
            .build();

    private final SynologyApiExecutor executor;

    public FileStationFileClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public CreateFolderResponse createFolder(CreateFolderRequest request) {
        // folder_path 和 name 必须一一对应，编码为官方要求的 JSON-like 数组字符串。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("folder_path", SynologyParameterEncoder.stringList(request.getFolderPaths()));
        parameters.put("name", SynologyParameterEncoder.stringList(request.getNames()));
        parameters.put("force_parent", SynologyParameterEncoder.booleanValue(request.getForceParent()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.CreateFolder", 2, "create", parameters, CreateFolderResponse.class);
    }

    public RenameResponse rename(RenameRequest request) {
        // path 和 name 必须一一对应，支持一次重命名多个文件/文件夹。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        parameters.put("name", SynologyParameterEncoder.stringList(request.getNames()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.Rename", 2, "rename", parameters, RenameResponse.class);
    }

    public TaskStartResponse deleteAsync(DeleteRequest request) {
        // start 是非阻塞删除，会返回 taskid，调用方可继续查询 status。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        parameters.put("accurate_progress", SynologyParameterEncoder.booleanValue(request.getAccurateProgress()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.Delete", 2, "start", parameters, TaskStartResponse.class);
    }

    public DeleteResponse delete(DeleteRequest request) {
        // 同步删除内部使用 start/status 任务模型，避开部分 DSM 对旧阻塞 delete 接口返回 HTML 的兼容问题。
        TaskStartResponse startResponse = deleteAsync(request);
        waitDeleteFinished(startResponse);
        return new DeleteResponse(true);
    }

    public TaskStatusResponse deleteStatus(String taskId) {
        return taskStatus("SYNO.FileStation.Delete", taskId);
    }

    public TaskStopResponse deleteStop(String taskId) {
        taskStop("SYNO.FileStation.Delete", taskId);
        return new TaskStopResponse(true);
    }

    public TaskStartResponse copy(CopyMoveRequest request) {
        return copyMove(request, Boolean.FALSE);
    }

    public TaskStartResponse move(CopyMoveRequest request) {
        return copyMove(request, Boolean.TRUE);
    }

    public TaskStatusResponse copyMoveStatus(String taskId) {
        return taskStatus("SYNO.FileStation.CopyMove", taskId);
    }

    public TaskStopResponse copyMoveStop(String taskId) {
        taskStop("SYNO.FileStation.CopyMove", taskId);
        return new TaskStopResponse(true);
    }

    private TaskStartResponse copyMove(CopyMoveRequest request, Boolean removeSrc) {
        // remove_src=false 表示复制，remove_src=true 表示移动，对外通过 copy/move 方法隐藏该底层开关。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        parameters.put("dest_folder_path", SynologyParameterEncoder.quoted(request.getDestFolderPath()));
        parameters.put("overwrite", SynologyParameterEncoder.booleanValue(request.getOverwrite()));
        parameters.put("remove_src", SynologyParameterEncoder.booleanValue(removeSrc));
        parameters.put("accurate_progress", SynologyParameterEncoder.booleanValue(request.getAccurateProgress()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.CopyMove", 3, "start", parameters, TaskStartResponse.class);
    }

    private TaskStatusResponse taskStatus(String apiName, String taskId) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskId));
        return executor.getAuthenticated("entry.cgi", apiName, 2, "status", parameters, TaskStatusResponse.class);
    }

    private void taskStop(String apiName, String taskId) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskId));
        executor.getAuthenticated("entry.cgi", apiName, 2, "stop", parameters, Object.class);
    }

    private void waitDeleteFinished(TaskStartResponse startResponse) {
        String taskId = requireTaskId(startResponse);
        // 复用通用轮询器，保持与 DirSize/Search 等任务型接口一致的等待语义。
        SynologyTaskPoller.wait(
                () -> deleteStatus(taskId),
                status -> Boolean.TRUE.equals(status.getFinished()),
                DELETE_WAIT_OPTIONS
        );
    }

    private String requireTaskId(TaskStartResponse startResponse) {
        if (startResponse == null || startResponse.getTaskid() == null || startResponse.getTaskid().trim().length() == 0) {
            throw new SynologyDsmException("delete task start response does not contain taskid");
        }
        return startResponse.getTaskid();
    }
}
