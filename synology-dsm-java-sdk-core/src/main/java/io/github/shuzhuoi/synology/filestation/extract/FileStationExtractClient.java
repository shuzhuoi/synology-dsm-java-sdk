package io.github.shuzhuoi.synology.filestation.extract;

import io.github.shuzhuoi.synology.filestation.task.SynologyTaskPoller;
import io.github.shuzhuoi.synology.filestation.task.TaskPollingOptions;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.task.TaskStopResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.Extract 客户端。
 * <p>
 * 解压属于非阻塞任务型 API：start 返回 taskid，status 查询进度，stop 取消任务。
 * list 可读取压缩包内部条目，用于按 item_id 部分解压。
 */
public class FileStationExtractClient {

    private static final String API_NAME = "SYNO.FileStation.Extract";
    private static final int API_VERSION = 2;

    private final SynologyApiExecutor executor;

    public FileStationExtractClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public TaskStartResponse start(ExtractStartRequest request) {
        // item_id 可限制只解压压缩包内指定条目；为空时由 DSM 解压全部内容。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("file_path", SynologyParameterEncoder.quoted(request.getFilePath()));
        parameters.put("dest_folder_path", SynologyParameterEncoder.quoted(request.getDestFolderPath()));
        parameters.put("overwrite", SynologyParameterEncoder.booleanValue(request.getOverwrite()));
        parameters.put("keep_dir", SynologyParameterEncoder.booleanValue(request.getKeepDir()));
        parameters.put("create_subfolder", SynologyParameterEncoder.booleanValue(request.getCreateSubfolder()));
        parameters.put("codepage", request.getCodepage());
        parameters.put("password", request.getPassword());
        parameters.put("item_id", SynologyParameterEncoder.integerList(request.getItemIds()));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "start", parameters, TaskStartResponse.class);
    }

    public ExtractStatusResponse status(String taskid) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "status", parameters, ExtractStatusResponse.class);
    }

    public TaskStopResponse stop(String taskid) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("taskid", SynologyParameterEncoder.quoted(taskid));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "stop", parameters, Object.class);
        return new TaskStopResponse(true);
    }

    public ExtractListResponse list(ExtractListRequest request) {
        // list 不启动任务，只读取压缩包目录；可配合 itemId 浏览压缩包内部子目录。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("file_path", SynologyParameterEncoder.quoted(request.getFilePath()));
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("codepage", request.getCodepage());
        parameters.put("password", request.getPassword());
        parameters.put("item_id", SynologyParameterEncoder.integerValue(request.getItemId()));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "list", parameters, ExtractListResponse.class);
    }

    /**
     * 轮询 status 直到解压任务完成。
     *
     * @param taskid  start 返回的任务 ID
     * @param options 轮询配置
     * @return 解压完成后的状态响应
     */
    public ExtractStatusResponse wait(String taskid, TaskPollingOptions options) {
        return SynologyTaskPoller.wait(
                () -> status(taskid),
                response -> Boolean.TRUE.equals(response.getFinished()),
                () -> stop(taskid),
                options
        );
    }
}
