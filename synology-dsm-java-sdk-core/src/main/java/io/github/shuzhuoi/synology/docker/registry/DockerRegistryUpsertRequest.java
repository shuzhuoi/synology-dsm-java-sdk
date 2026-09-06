package io.github.shuzhuoi.synology.docker.registry;

import lombok.Getter;

/**
 * SYNO.Docker.Registry create / set 方法的请求参数。
 * <p>
 * 对应 DSM 容器管理器「注册表 → 设置 → 新增/编辑」表单：
 * name 为唯一键（set 时定位要修改的条目），认证账号可选。
 */
@Getter
public class DockerRegistryUpsertRequest {

    /**
     * 注册表显示名（必填，唯一键）。
     */
    private final String name;
    /**
     * 注册表地址（必填），例如 https://ghcr.io。
     */
    private final String url;
    /**
     * 是否信任自签名证书。
     */
    private final Boolean enableTrustSsc;
    /**
     * 认证用户名，匿名访问可不填。
     */
    private final String username;
    /**
     * 认证密码，匿名访问可不填。
     */
    private final String password;

    private DockerRegistryUpsertRequest(Builder builder) {
        this.name = builder.name;
        this.url = builder.url;
        this.enableTrustSsc = builder.enableTrustSsc;
        this.username = builder.username;
        this.password = builder.password;
    }

    public static Builder builder(String name, String url) {
        return new Builder(name, url);
    }

    public static class Builder {

        private final String name;
        private final String url;
        private Boolean enableTrustSsc;
        private String username;
        private String password;

        private Builder(String name, String url) {
            this.name = name;
            this.url = url;
        }

        /**
         * 设置是否信任自签名证书。
         */
        public Builder enableTrustSsc(Boolean enableTrustSsc) {
            this.enableTrustSsc = enableTrustSsc;
            return this;
        }

        /**
         * 设置认证用户名。
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * 设置认证密码。
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public DockerRegistryUpsertRequest build() {
            return new DockerRegistryUpsertRequest(this);
        }
    }
}
