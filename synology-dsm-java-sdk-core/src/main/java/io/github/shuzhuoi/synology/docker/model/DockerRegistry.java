package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Registry get 返回的单个注册表配置。
 * <p>
 * 对应 DSM 容器管理器「注册表 → 设置」页面维护的条目：
 * Docker Hub / 自定义 registry 地址、认证账号、信任自签名证书、镜像加速等。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerRegistry {

    /**
     * 注册表显示名，唯一键，也是 set / using / delete 的定位参数。
     */
    private String name;
    /**
     * 注册表地址，例如 https://registry.hub.docker.com。
     */
    private String url;
    /**
     * 是否信任自签名证书（注意字段名大写 SSC）。
     */
    @SynologyJsonProperty("enable_trust_SSC")
    private Boolean enableTrustSsc;
    /**
     * 是否启用镜像加速。
     */
    @SynologyJsonProperty("enable_registry_mirror")
    private Boolean enableRegistryMirror;
    /**
     * 镜像加速地址列表。
     */
    @SynologyJsonProperty("mirror_urls")
    private List<String> mirrorUrls;
    /**
     * 是否为 DSM 内置条目（如 Docker Hub），内置条目不可删除。
     */
    private Boolean syno;
}
