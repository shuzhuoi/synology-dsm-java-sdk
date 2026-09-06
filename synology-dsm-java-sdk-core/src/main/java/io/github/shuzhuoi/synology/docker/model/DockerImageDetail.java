package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Image get 返回的单个镜像详情。
 * <p>
 * 与 {@link DockerImage} 字段基本一致；官方未公开该响应示例，
 * 字段以 list 响应字段推定并做宽松映射（未知字段忽略），
 * 真机验证后如有出入只需补充字段。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerImageDetail {

    /**
     * 镜像 ID，例如 sha256:14300de7e087...。
     */
    private String id;
    /**
     * 仓库名称，例如 caddy、grafana/grafana。
     */
    private String repository;
    /**
     * 镜像标签列表。
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
     * 镜像描述。
     */
    private String description;
    /**
     * 本地镜像摘要。
     */
    private String digest;
    /**
     * 远端仓库摘要。
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
