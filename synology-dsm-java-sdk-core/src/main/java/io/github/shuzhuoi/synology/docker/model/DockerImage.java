package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Image list 返回的单个镜像信息。
 * <p>
 * 镜像以「仓库 repository + 标签列表 tags」定位，一个镜像对象可挂多个标签；
 * id 为 Docker 镜像摘要（sha256 前缀）。字段名以真机响应为准（全小写下划线风格）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerImage {

    /**
     * 镜像 ID，例如 sha256:14300de7e087...。
     */
    private String id;
    /**
     * 仓库名称，例如 caddy、grafana/grafana。
     */
    private String repository;
    /**
     * 镜像标签列表，例如 ["alpine", "latest"]。
     */
    private List<String> tags;
    /**
     * 镜像大小，单位字节。
     */
    private Long size;
    /**
     * 镜像虚拟大小（含分层父镜像），单位字节。
     */
    @SynologyJsonProperty("virtual_size")
    private Long virtualSize;
    /**
     * 创建时间，unix 秒。
     */
    private Long created;
    /**
     * 镜像描述，通常为空字符串。
     */
    private String description;
    /**
     * 本地镜像摘要，通常为空字符串。
     */
    private String digest;
    /**
     * 远端仓库摘要，通常为空字符串。
     */
    @SynologyJsonProperty("remote_digest")
    private String remoteDigest;
    /**
     * 是否存在可用更新。
     */
    private Boolean upgradable;
    /**
     * 是否为 DSM 系统自带镜像。
     */
    @SynologyJsonProperty("is_ddsm")
    private Boolean isDdsm;
}
