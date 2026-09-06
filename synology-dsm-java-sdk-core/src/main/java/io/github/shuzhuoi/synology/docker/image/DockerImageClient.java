package io.github.shuzhuoi.synology.docker.image;

import io.github.shuzhuoi.synology.docker.DockerApi;
import io.github.shuzhuoi.synology.docker.model.DockerImageDetail;
import io.github.shuzhuoi.synology.docker.model.DockerImageListResponse;
import io.github.shuzhuoi.synology.docker.model.DockerImagePullStartResponse;
import io.github.shuzhuoi.synology.docker.model.DockerImagePullStatusResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.Docker.Image 客户端。
 * <p>
 * 提供本地镜像的列表 / 详情查询、删除、清理（prune）以及从镜像仓库拉取
 * （pull_start + pull_status 两步异步流程）。
 * <p>
 * 参数编码约定（契约声明 requestFormat=JSON）：
 * 字符串参数以 JSON 字符串形式传输（带引号）；offset / limit 数值参数与
 * show_dsm 布尔参数传普通字符串。社区实现（N4S4 synology-api、
 * atom2ueki/mcp-server-synology）对镜像参数普遍传裸值且真机可用，
 * DSM 服务端对两种编码均兼容，这里统一采用与官方契约一致的引号编码。
 * 常见错误码：105 权限不足、400 无效参数。
 */
public class DockerImageClient {

    private final SynologyApiExecutor executor;

    public DockerImageClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询全部本地镜像列表（offset=0、limit=-1、不包含 DSM 系统镜像）。
     *
     * @return 镜像列表与总数
     */
    public DockerImageListResponse list() {
        return list(DockerImageListRequest.builder().build());
    }

    /**
     * 分页查询本地镜像列表。
     *
     * @param request 查询请求
     * @return 镜像列表与总数
     */
    public DockerImageListResponse list(DockerImageListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // offset / limit 为普通整数字符串，show_dsm 为普通小写布尔字符串。
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("show_dsm", SynologyParameterEncoder.booleanValue(request.getShowDsm()));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.IMAGE_API,
                DockerApi.IMAGE_VERSION,
                "list",
                parameters,
                DockerImageListResponse.class
        );
    }

    /**
     * 查询指定镜像的详情（tag 默认 latest）。
     *
     * @param repository 仓库名称，例如 caddy、grafana/grafana
     * @return 镜像详情
     */
    public DockerImageDetail get(String repository) {
        return get(repository, "latest");
    }

    /**
     * 查询指定镜像的详情。
     * <p>
     * 镜像以「仓库名:标签」组合定位，通过 image 参数一次性传输。
     *
     * @param repository 仓库名称，例如 caddy、grafana/grafana
     * @param tag        镜像标签，例如 alpine
     * @return 镜像详情
     */
    public DockerImageDetail get(String repository, String tag) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // image 参数为「仓库名:标签」组合，以 JSON 字符串形式传输（带引号）。
        parameters.put("image", SynologyParameterEncoder.quoted(repository + ":" + tag));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.IMAGE_API,
                DockerApi.IMAGE_VERSION,
                "get",
                parameters,
                DockerImageDetail.class
        );
    }

    /**
     * 删除指定镜像（tag 默认 latest）。
     *
     * @param repository 仓库名称
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String repository) {
        return delete(repository, "latest");
    }

    /**
     * 删除指定镜像。
     * <p>
     * 通过 name（仓库名）+ tag 两个参数定位镜像。
     *
     * @param repository 仓库名称
     * @param tag        镜像标签
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String repository, String tag) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // name / tag 均以 JSON 字符串形式传输（带引号）。
        parameters.put("name", SynologyParameterEncoder.quoted(repository));
        parameters.put("tag", SynologyParameterEncoder.quoted(tag));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.IMAGE_API,
                DockerApi.IMAGE_VERSION,
                "delete",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 清理未被任何容器引用的镜像（docker image prune 语义）。
     * <p>
     * 该操作会真实删除悬空镜像，调用前请确认。
     *
     * @return 操作结果
     */
    public SynologyOperationResponse prune() {
        // prune 无额外业务参数。
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.IMAGE_API,
                DockerApi.IMAGE_VERSION,
                "prune",
                new LinkedHashMap<String, String>(),
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 发起镜像拉取（tag 默认 latest）。
     * <p>
     * 拉取是异步操作：本方法只负责发起并返回任务 ID，
     * 需继续调用 {@link #pullStatus(String)} 轮询直到任务结束。
     *
     * @param repository 仓库名称，例如 nginx、grafana/grafana
     * @return 拉取任务信息（含 task_id）
     */
    public DockerImagePullStartResponse pullStart(String repository) {
        return pullStart(repository, "latest");
    }

    /**
     * 发起镜像拉取。
     * <p>
     * 拉取是异步操作：本方法只负责发起并返回任务 ID，
     * 需继续调用 {@link #pullStatus(String)} 轮询直到任务结束。
     *
     * @param repository 仓库名称，例如 nginx、grafana/grafana
     * @param tag        镜像标签，例如 latest
     * @return 拉取任务信息（含 task_id）
     */
    public DockerImagePullStartResponse pullStart(String repository, String tag) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // repository / tag 以 JSON 字符串形式传输（带引号）。
        parameters.put("repository", SynologyParameterEncoder.quoted(repository));
        parameters.put("tag", SynologyParameterEncoder.quoted(tag));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.IMAGE_API,
                DockerApi.IMAGE_VERSION,
                "pull_start",
                parameters,
                DockerImagePullStartResponse.class
        );
    }

    /**
     * 查询镜像拉取进度。
     *
     * @param taskId pull_start 返回的任务 ID
     * @return 拉取进度信息
     */
    public DockerImagePullStatusResponse pullStatus(String taskId) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // task_id 以 JSON 字符串形式传输（带引号）。
        parameters.put("task_id", SynologyParameterEncoder.quoted(taskId));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.IMAGE_API,
                DockerApi.IMAGE_VERSION,
                "pull_status",
                parameters,
                DockerImagePullStatusResponse.class
        );
    }
}
