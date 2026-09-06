package io.github.shuzhuoi.synology.downloadstation.task;

import io.github.shuzhuoi.synology.downloadstation.DownloadStationApi;
import io.github.shuzhuoi.synology.downloadstation.model.TaskAdditionalField;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.internal.request.SynologyApiRequest;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SYNO.DownloadStation2.Task 客户端。
 * <p>
 * 下载任务的增删改查、暂停恢复与目的地修改。
 * 常见错误码：400 无效参数、401 无效任务 ID、402 无效任务动作、
 * 403 无效文件格式、404 上传失败、405 超时（由 SynologyApiException 统一抛出）。
 */
public class DownloadStationTaskClient {

    private final SynologyApiExecutor executor;

    public DownloadStationTaskClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 分页查询下载任务列表。
     *
     * @param request 查询请求
     * @return 任务列表与总数
     */
    public TaskListResponse list(TaskListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("additional", additionalParameter(request.getAdditionals()));
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_API, DownloadStationApi.TASK_VERSION, "list", parameters, TaskListResponse.class);
    }

    /**
     * 查询指定任务的详细信息。
     *
     * @param request 查询请求
     * @return 任务详情列表
     */
    public TaskGetInfoResponse getInfo(TaskGetInfoRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // 官方约定多个任务 ID 以逗号分隔，不使用 JSON 引号。
        parameters.put("id", SynologyParameterEncoder.join(request.getIds()));
        parameters.put("additional", additionalParameter(request.getAdditionals()));
        return executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_API, DownloadStationApi.TASK_VERSION, "getinfo", parameters, TaskGetInfoResponse.class);
    }

    /**
     * 通过 URL 创建下载任务（http/https/ftp/magnet 等）。
     *
     * @param request 创建请求
     * @return 操作结果
     */
    public SynologyOperationResponse create(TaskCreateRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // 多个下载链接以逗号分隔。
        parameters.put("uri", SynologyParameterEncoder.join(request.getUris()));
        // 目的地路径官方要求以 JSON 字符串形式传输，即带引号。
        parameters.put("destination", SynologyParameterEncoder.quoted(request.getDestination()));
        parameters.put("unzip_password", request.getUnzipPassword());
        parameters.put("create_list", SynologyParameterEncoder.booleanValue(request.getCreateList()));
        executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_API, DownloadStationApi.TASK_VERSION, "create", parameters, Object.class);
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 通过上传本地 .torrent 文件创建下载任务。
     *
     * @param request 创建请求
     * @return 操作结果
     */
    public SynologyOperationResponse createTorrent(TaskCreateTorrentRequest request) {
        // 种子上传走 multipart/form-data，普通参数和文件内容都放在表单 part 中。
        SynologyApiRequest apiRequest = SynologyApiRequest.builder()
                .path("entry.cgi")
                .apiName(DownloadStationApi.TASK_API)
                .version(DownloadStationApi.TASK_VERSION)
                .method("create")
                .authenticated(true)
                .parameter("destination", SynologyParameterEncoder.quoted(request.getDestination()))
                .parameter("unzip_password", request.getUnzipPassword())
                .parameter("create_list", SynologyParameterEncoder.booleanValue(request.getCreateList()))
                .responseType(Object.class)
                .httpMethod(SynologyHttpMethod.POST)
                // 文件字段名按 DSM entry.cgi 惯例使用 file，官方文档要求 file part 放在最后。
                .multipartPart(SynologyMultipartPart.file("file", request.getTorrentFile()))
                .build();
        executor.executeAuthenticated(apiRequest);
        return new SynologyOperationResponse(true);
    }

    /**
     * 删除单个下载任务，不强制完成。
     *
     * @param id 任务 ID
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String id) {
        return delete(java.util.Collections.singletonList(id), null);
    }

    /**
     * 删除一个或多个下载任务。
     *
     * @param ids           任务 ID 列表
     * @param forceComplete 未完成任务是否先强制完成再删除
     * @return 操作结果
     */
    public SynologyOperationResponse delete(List<String> ids, Boolean forceComplete) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", SynologyParameterEncoder.join(ids));
        parameters.put("force_complete", SynologyParameterEncoder.booleanValue(forceComplete));
        executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_API, DownloadStationApi.TASK_VERSION, "delete", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    /**
     * 暂停单个下载任务。
     *
     * @param id 任务 ID
     * @return 操作结果
     */
    public SynologyOperationResponse pause(String id) {
        return pause(java.util.Collections.singletonList(id));
    }

    /**
     * 暂停一个或多个下载任务。
     *
     * @param ids 任务 ID 列表
     * @return 操作结果
     */
    public SynologyOperationResponse pause(List<String> ids) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", SynologyParameterEncoder.join(ids));
        executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_API, DownloadStationApi.TASK_VERSION, "pause", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    /**
     * 恢复单个下载任务。
     *
     * @param id 任务 ID
     * @return 操作结果
     */
    public SynologyOperationResponse resume(String id) {
        return resume(java.util.Collections.singletonList(id));
    }

    /**
     * 恢复一个或多个下载任务。
     *
     * @param ids 任务 ID 列表
     * @return 操作结果
     */
    public SynologyOperationResponse resume(List<String> ids) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", SynologyParameterEncoder.join(ids));
        executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_API, DownloadStationApi.TASK_VERSION, "resume", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    /**
     * 修改任务下载目的地。
     *
     * @param id          任务 ID
     * @param destination 新的目的地路径
     * @return 操作结果
     */
    public SynologyOperationResponse edit(String id, String destination) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", id);
        // 目的地路径官方要求以 JSON 字符串形式传输，即带引号。
        parameters.put("destination", SynologyParameterEncoder.quoted(destination));
        executor.getAuthenticated("entry.cgi", DownloadStationApi.TASK_API, DownloadStationApi.TASK_VERSION, "edit", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    /**
     * 把扩展信息块枚举列表编码为 JSON-like 数组字符串，例如 ["detail","transfer"]。
     */
    private String additionalParameter(List<TaskAdditionalField> additionals) {
        if (additionals == null || additionals.isEmpty()) {
            return null;
        }
        List<String> names = new ArrayList<String>();
        for (TaskAdditionalField additional : additionals) {
            if (additional != null) {
                names.add(additional.getValue());
            }
        }
        return SynologyParameterEncoder.stringList(names);
    }
}
