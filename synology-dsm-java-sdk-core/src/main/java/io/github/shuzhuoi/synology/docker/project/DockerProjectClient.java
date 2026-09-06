package io.github.shuzhuoi.synology.docker.project;

import io.github.shuzhuoi.synology.docker.DockerApi;
import io.github.shuzhuoi.synology.docker.model.DockerProject;
import io.github.shuzhuoi.synology.docker.model.DockerProjectDetail;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.Docker.Project 客户端。
 * <p>
 * 提供 Compose 项目的列表 / 详情查询以及启动、停止、重启、清理、删除等生命周期操作。
 * 项目以 UUID（id）定位，可先通过 {@link #list()} 获取全部项目再按名称筛选目标 id。
 * <p>
 * 参数编码约定（契约声明 requestFormat=JSON）：
 * id 参数以 JSON 字符串形式传输（带引号）；preserve_content 布尔参数传普通字符串。
 * 社区实现（N4S4 synology-api、atom2ueki/mcp-server-synology）对 id 普遍传裸值
 * 且真机可用，DSM 前端抓包则为引号形态，服务端对两种编码均兼容，
 * 这里统一采用与官方契约一致的引号编码。
 * 常见错误码：105 权限不足、400 无效参数。
 */
public class DockerProjectClient {

    private final SynologyApiExecutor executor;

    public DockerProjectClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询全部 Compose 项目。
     * <p>
     * 响应的 data 是以项目 UUID 为键的 map，值 {@link DockerProject} 为项目摘要
     * （含名称、路径、状态、容器 ID 列表、服务门户配置等）。
     *
     * @return 项目 UUID 到项目摘要的映射，保持 DSM 返回顺序
     */
    public Map<String, DockerProject> list() {
        // list 无业务参数。
        return executor.getAuthenticatedMap(
                "entry.cgi",
                DockerApi.PROJECT_API,
                DockerApi.PROJECT_VERSION,
                "list",
                new LinkedHashMap<String, String>(),
                DockerProject.class
        );
    }

    /**
     * 按名称查找项目摘要。
     * <p>
     * DSM 未提供按名称查询的接口，这里基于 {@link #list()} 的结果本地筛选。
     *
     * @param name 项目名称（Container Manager 中显示的名称）
     * @return 匹配的项目摘要，未找到时返回 null
     */
    public DockerProject findByName(String name) {
        Map<String, DockerProject> projects = list();
        if (projects == null || name == null) {
            return null;
        }
        for (DockerProject project : projects.values()) {
            if (project != null && name.equals(project.getName())) {
                return project;
            }
        }
        return null;
    }

    /**
     * 查询指定项目的详情（含容器 inspect 结构与 compose 文件内容）。
     *
     * @param projectId 项目 UUID
     * @return 项目详情
     */
    public DockerProjectDetail get(String projectId) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // id 参数以 JSON 字符串形式传输（带引号）。
        parameters.put("id", SynologyParameterEncoder.quoted(projectId));
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.PROJECT_API,
                DockerApi.PROJECT_VERSION,
                "get",
                parameters,
                DockerProjectDetail.class
        );
    }

    /**
     * 启动指定项目（docker compose start 语义，启动项目内已存在的容器）。
     *
     * @param projectId 项目 UUID
     * @return 操作结果
     */
    public SynologyOperationResponse start(String projectId) {
        return lifecycle("start", projectId);
    }

    /**
     * 停止指定项目（docker compose stop 语义，停止容器但不移除）。
     *
     * @param projectId 项目 UUID
     * @return 操作结果
     */
    public SynologyOperationResponse stop(String projectId) {
        return lifecycle("stop", projectId);
    }

    /**
     * 重启指定项目（docker compose restart 语义）。
     *
     * @param projectId 项目 UUID
     * @return 操作结果
     */
    public SynologyOperationResponse restart(String projectId) {
        return lifecycle("restart", projectId);
    }

    /**
     * 清理指定项目（docker compose down 语义，停止并移除项目创建的容器、网络等）。
     *
     * @param projectId 项目 UUID
     * @return 操作结果
     */
    public SynologyOperationResponse clean(String projectId) {
        return lifecycle("clean", projectId);
    }

    /**
     * 删除指定项目（不保留 compose 文件）。
     *
     * @param projectId 项目 UUID
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String projectId) {
        return delete(projectId, Boolean.FALSE);
    }

    /**
     * 删除指定项目。
     *
     * @param projectId       项目 UUID
     * @param preserveContent 是否保留项目目录下的 compose 文件
     * @return 操作结果
     */
    public SynologyOperationResponse delete(String projectId, Boolean preserveContent) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // id 以 JSON 字符串形式传输（带引号），preserve_content 为普通小写布尔字符串。
        parameters.put("id", SynologyParameterEncoder.quoted(projectId));
        parameters.put("preserve_content", SynologyParameterEncoder.booleanValue(preserveContent));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.PROJECT_API,
                DockerApi.PROJECT_VERSION,
                "delete",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 执行 start / stop / restart / clean 等仅携带 id 的项目生命周期操作。
     */
    private SynologyOperationResponse lifecycle(String method, String projectId) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        // id 参数以 JSON 字符串形式传输（带引号）。
        parameters.put("id", SynologyParameterEncoder.quoted(projectId));
        executor.getAuthenticated(
                "entry.cgi",
                DockerApi.PROJECT_API,
                DockerApi.PROJECT_VERSION,
                method,
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
