package io.github.shuzhuoi.synology.docker.container;

import io.github.shuzhuoi.synology.docker.DockerApi;
import io.github.shuzhuoi.synology.docker.model.DockerContainerDetail;
import io.github.shuzhuoi.synology.docker.model.DockerContainerListResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.Docker.Container 客户端。
 * <p>
 * 提供容器列表查询、详情查询（inspect）以及启动、停止、重启、删除等生命周期操作。
 * 常见错误码：105 权限不足（SYNO.Docker.* 需要管理员或 Container Manager 权限）、
 * 400 无效参数（由 SynologyApiException 统一抛出）。
 */
public class DockerContainerClient {

    private final SynologyApiExecutor executor;

    public DockerContainerClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询全部容器列表（offset=0、limit=-1）。
     *
     * @return 容器列表与总数
     */
    public DockerContainerListResponse list() {
        return list(DockerContainerListRequest.builder().build());
    }

    /**
     * 分页查询容器列表，支持按运行状态过滤。
     *
     * @param request 查询请求
     * @return 容器列表与总数
     */
    public DockerContainerListResponse list(DockerContainerListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // offset / limit 为普通整数字符串，不使用 JSON 编码。
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        if (request.getType() != null) {
            parameters.put("type", request.getType().getValue());
        }
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_API,
                DockerApi.CONTAINER_VERSION,
                "list",
                parameters,
                DockerContainerListResponse.class
        );
    }

    /**
     * 查询指定容器的完整详情（Docker inspect 结构，含端口映射、环境变量等）。
     *
     * @param name 容器名称
     * @return 容器详情
     */
    public DockerContainerDetail get(String name) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // Container Manager 契约声明 requestFormat=JSON，name 参数需以 JSON 字符串形式传输（带引号）。
        parameters.put("name", SynologyParameterEncoder.quoted(name));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_API,
                DockerApi.CONTAINER_VERSION,
                "get",
                parameters,
                DockerContainerDetail.class
        );
    }

    /**
     * 启动指定容器。
     *
     * @param name 容器名称
     * @return 操作结果
     */
    public SynologyOperationResponse start(String name) {
        // name 参数以 JSON 字符串形式传输。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("name", SynologyParameterEncoder.quoted(name));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_API,
                DockerApi.CONTAINER_VERSION,
                "start",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 停止指定容器。
     *
     * @param name 容器名称
     * @return 操作结果
     */
    public SynologyOperationResponse stop(String name) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("name", SynologyParameterEncoder.quoted(name));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_API,
                DockerApi.CONTAINER_VERSION,
                "stop",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 重启指定容器。
     *
     * @param name 容器名称
     * @return 操作结果
     */
    public SynologyOperationResponse restart(String name) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("name", SynologyParameterEncoder.quoted(name));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_API,
                DockerApi.CONTAINER_VERSION,
                "restart",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 删除指定容器（不强制删除，保留容器配置档案）。
     *
     * @param name 容器名称
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String name) {
        return delete(name, Boolean.FALSE, Boolean.FALSE);
    }

    /**
     * 删除指定容器。
     *
     * @param name            容器名称
     * @param force           是否强制删除运行中的容器
     * @param preserveProfile 是否保留容器配置档案（DSM UI 删除时的选项）
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String name, Boolean force, Boolean preserveProfile) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("name", SynologyParameterEncoder.quoted(name));
        // force / preserve_profile 为普通小写布尔字符串。
        parameters.put("force", SynologyParameterEncoder.booleanValue(force));
        parameters.put("preserve_profile", SynologyParameterEncoder.booleanValue(preserveProfile));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_API,
                DockerApi.CONTAINER_VERSION,
                "delete",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
