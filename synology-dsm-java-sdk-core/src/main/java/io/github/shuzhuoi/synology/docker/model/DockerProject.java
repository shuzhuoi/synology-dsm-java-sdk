package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Project list 返回的单个 Compose 项目信息。
 * <p>
 * 项目对应 Container Manager UI 中的「项目」（docker compose 技术栈）。
 * list 响应的 data 是以项目 UUID 为键的 map，值即本结构；
 * status 常见取值：RUNNING / STOPPED / ERROR 等。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerProject {

    /**
     * 项目 UUID，例如 187b2816-fd6c-4f87-b178-6d94806c7404。
     */
    private String id;
    /**
     * 项目名称，例如 pihole。
     */
    private String name;
    /**
     * 项目在 NAS 上的绝对路径，例如 /volume1/docker/test。
     */
    private String path;
    /**
     * 项目相对共享文件夹的路径，例如 /docker/test。
     */
    @SynologyJsonProperty("share_path")
    private String sharePath;
    /**
     * 项目状态，例如 RUNNING / STOPPED / ERROR。
     */
    private String status;
    /**
     * 项目附加状态描述，正常为空字符串。
     */
    private String state;
    /**
     * 项目内全部容器的 ID 列表（64 位十六进制）。
     */
    @SynologyJsonProperty("containerIds")
    private List<String> containerIds;
    /**
     * 是否启用了反向代理服务门户。
     */
    @SynologyJsonProperty("enable_service_portal")
    private Boolean enableServicePortal;
    /**
     * 服务门户名称，未启用时为空字符串。
     */
    @SynologyJsonProperty("service_portal_name")
    private String servicePortalName;
    /**
     * 服务门户端口，未启用时为 0。
     */
    @SynologyJsonProperty("service_portal_port")
    private Integer servicePortalPort;
    /**
     * 服务门户协议，例如 http / https，未启用时为空字符串。
     */
    @SynologyJsonProperty("service_portal_protocol")
    private String servicePortalProtocol;
    /**
     * 服务门户条目列表，未启用时为空数组。
     */
    private List<DockerProjectService> services;
    /**
     * 是否为 DSM 套件自带项目。
     */
    @SynologyJsonProperty("is_package")
    private Boolean isPackage;
    /**
     * 创建时间，ISO 8601 格式字符串。
     */
    @SynologyJsonProperty("created_at")
    private String createdAt;
    /**
     * 最后更新时间，ISO 8601 格式字符串。
     */
    @SynologyJsonProperty("updated_at")
    private String updatedAt;
    /**
     * 项目配置版本号。
     */
    private Integer version;
}
