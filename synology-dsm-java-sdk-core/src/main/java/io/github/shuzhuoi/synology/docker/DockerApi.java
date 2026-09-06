package io.github.shuzhuoi.synology.docker;

/**
 * Docker / Container Manager API 名称与版本常量。
 * <p>
 * DSM 7.2+ 的 Container Manager 使用 SYNO.Docker.* 系列接口，统一走 entry.cgi。
 * 注意：以下版本号依据 DSM 7.2 社区文档与真机 SYNO.API.Info 抓取推定（maxVersion 均为 1），
 * 请以 {@code DockerCoverageExample} 启动时打印的 SYNO.API.Info 探测结果为准，
 * 如有偏差只需修正本类中的常量，无需改动各客户端。
 * 权限要求：SYNO.Docker.* 通常需要管理员或具有 Container Manager 权限的账户，
 * 普通账户调用会返回 105（insufficient privilege）。
 */
public final class DockerApi {

    /**
     * 容器管理 API：list / get / start / stop / restart / delete 等。
     */
    public static final String CONTAINER_API = "SYNO.Docker.Container";
    /**
     * Container API 版本。
     */
    public static final int CONTAINER_VERSION = 1;

    /**
     * 容器资源实时监控 API（CPU / 内存占用）。
     */
    public static final String CONTAINER_RESOURCE_API = "SYNO.Docker.Container.Resource";
    /**
     * Container.Resource API 版本。
     */
    public static final int CONTAINER_RESOURCE_VERSION = 1;

    /**
     * 容器日志查询 API。
     * <p>
     * 注意与 SYNO.Docker.Log（Docker 守护进程全局日志）是两个不同的 API。
     */
    public static final String CONTAINER_LOG_API = "SYNO.Docker.Container.Log";
    /**
     * Container.Log API 版本。
     */
    public static final int CONTAINER_LOG_VERSION = 1;

    /**
     * 镜像管理 API：list / get / delete / prune / pull_start / pull_status 等。
     * <p>
     * 镜像以「仓库名:标签」定位，list 响应的 tags 是数组（一个镜像可挂多个标签）。
     */
    public static final String IMAGE_API = "SYNO.Docker.Image";
    /**
     * Image API 版本。
     */
    public static final int IMAGE_VERSION = 1;

    /**
     * Compose 项目管理 API：list / get / start / stop / restart / clean / delete 等。
     * <p>
     * 项目对应 Container Manager UI 中的「项目」（docker compose 技术栈），
     * 以 UUID（id）定位；list 响应的 data 是以项目 UUID 为键的 map。
     */
    public static final String PROJECT_API = "SYNO.Docker.Project";
    /**
     * Project API 版本。
     */
    public static final int PROJECT_VERSION = 1;

    private DockerApi() {
    }
}
