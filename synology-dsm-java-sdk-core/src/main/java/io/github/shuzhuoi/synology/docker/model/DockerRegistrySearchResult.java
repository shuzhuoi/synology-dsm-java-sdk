package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.Docker.Registry search 返回的单条镜像搜索结果。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerRegistrySearchResult {

    /**
     * 镜像名称，例如 caddy、abiosoft/caddy。
     */
    private String name;
    /**
     * 镜像描述。
     */
    private String description;
    /**
     * 下载次数。
     */
    private Long downloads;
    /**
     * 收藏数。
     */
    @SynologyJsonProperty("star_count")
    private Long starCount;
    /**
     * 是否官方镜像。
     */
    @SynologyJsonProperty("is_official")
    private Boolean isOfficial;
    /**
     * 是否自动构建镜像。
     */
    @SynologyJsonProperty("is_automated")
    private Boolean isAutomated;
    /**
     * 所属注册表地址。
     */
    private String registry;
}
