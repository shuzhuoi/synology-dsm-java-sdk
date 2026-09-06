package io.github.shuzhuoi.synology.docker.network;

import lombok.Getter;

/**
 * SYNO.Docker.Network create 方法的请求参数。
 */
@Getter
public class DockerNetworkCreateRequest {

    /**
     * 网络名称（必填）。
     */
    private final String name;
    /**
     * 网络驱动，例如 bridge / host，默认由 DSM 决定（通常为 bridge）。
     */
    private final String driver;
    /**
     * 是否启用 IPv6。
     */
    private final Boolean enableIpv6;
    /**
     * 子网 CIDR，例如 172.28.0.0/16。
     */
    private final String subnet;
    /**
     * 网关 IP，例如 172.28.0.1。
     */
    private final String gateway;
    /**
     * 可分配 IP 范围 CIDR。
     */
    private final String iprange;

    private DockerNetworkCreateRequest(Builder builder) {
        this.name = builder.name;
        this.driver = builder.driver;
        this.enableIpv6 = builder.enableIpv6;
        this.subnet = builder.subnet;
        this.gateway = builder.gateway;
        this.iprange = builder.iprange;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private final String name;
        private String driver;
        private Boolean enableIpv6;
        private String subnet;
        private String gateway;
        private String iprange;

        private Builder(String name) {
            this.name = name;
        }

        /**
         * 设置网络驱动，例如 bridge / host。
         */
        public Builder driver(String driver) {
            this.driver = driver;
            return this;
        }

        /**
         * 设置是否启用 IPv6。
         */
        public Builder enableIpv6(Boolean enableIpv6) {
            this.enableIpv6 = enableIpv6;
            return this;
        }

        /**
         * 设置子网 CIDR。
         */
        public Builder subnet(String subnet) {
            this.subnet = subnet;
            return this;
        }

        /**
         * 设置网关 IP。
         */
        public Builder gateway(String gateway) {
            this.gateway = gateway;
            return this;
        }

        /**
         * 设置可分配 IP 范围 CIDR。
         */
        public Builder iprange(String iprange) {
            this.iprange = iprange;
            return this;
        }

        public DockerNetworkCreateRequest build() {
            return new DockerNetworkCreateRequest(this);
        }
    }
}
