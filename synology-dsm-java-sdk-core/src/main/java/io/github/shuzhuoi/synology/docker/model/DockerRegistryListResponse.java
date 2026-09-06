package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Registry get 方法的响应（注册表配置列表）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerRegistryListResponse {

    /**
     * 注册表配置列表。
     */
    private List<DockerRegistry> registries;
    /**
     * 当前使用的注册表显示名。
     */
    private String using;
    /**
     * 配置总数。
     */
    private Integer total;
    /**
     * 当前偏移量。
     */
    private Integer offset;
}
