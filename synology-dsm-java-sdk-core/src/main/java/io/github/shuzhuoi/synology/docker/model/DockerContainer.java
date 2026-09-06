package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.Docker.Container list 返回的单个容器信息。
 * <p>
 * 响应字段大小写混合（小写字段来自 DSM 扩展，大写字段来自 Docker inspect 结构），
 * 请以真机响应为准。端口映射等更完整的信息需调用 get（inspect 详情）获取。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainer {

    /**
     * 容器 ID，64 位十六进制字符串（docker container ID）。
     */
    private String id;
    /**
     * 容器名称。
     */
    private String name;
    /**
     * 容器镜像名称，例如 nginx:latest。
     */
    private String image;
    /**
     * 容器状态，例如 running / exited。
     */
    private String status;
    /**
     * 运行时长可读字符串，例如 "Up 3 days"。
     */
    @SynologyJsonProperty("up_status")
    private String upStatus;
    /**
     * 创建时间，unix 秒。
     */
    private Long created;
    /**
     * 是否为 DSM 套件自带的容器。
     */
    @SynologyJsonProperty("is_package")
    private Boolean isPackage;
    /**
     * 是否为 DDSM（Docker DSM）容器。
     */
    @SynologyJsonProperty("is_ddsm")
    private Boolean isDdsm;
    /**
     * 是否启用了反向代理服务门户。
     */
    @SynologyJsonProperty("enable_service_portal")
    private Boolean enableServicePortal;
    /**
     * 容器启动命令。
     */
    private String cmd;
    /**
     * 容器运行状态详情（Docker inspect 的 State 结构）。
     */
    @SynologyJsonProperty("State")
    private DockerContainerState state;
    /**
     * 网络配置摘要（Docker inspect 的 NetworkSettings 结构）。
     */
    @SynologyJsonProperty("NetworkSettings")
    private DockerContainerNetworkSettings networkSettings;
}
