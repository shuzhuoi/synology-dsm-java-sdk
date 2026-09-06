package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.Docker.Project 项目内的服务门户条目（services 数组元素）。
 * <p>
 * 项目启用网页门户（反向代理）时才会出现该条目。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerProjectService {

    /**
     * 服务展示名称，例如 "test (project)"。
     */
    @SynologyJsonProperty("display_name")
    private String displayName;
    /**
     * 服务 ID，格式为 Docker-Project-{项目UUID}。
     */
    private String id;
    /**
     * 服务标识，与 id 相同。
     */
    private String service;
    /**
     * 反向代理目标地址，例如 http://127.0.0.1:53。
     */
    @SynologyJsonProperty("proxy_target")
    private String proxyTarget;
    /**
     * 服务类型，例如 reverse_proxy。
     */
    private String type;
}
