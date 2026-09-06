package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 容器在单个 Docker 网络中的连接信息。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerNetwork {

    /**
     * 容器在该网络中的 IP 地址。
     */
    @SynologyJsonProperty("IPAddress")
    private String ipAddress;
    /**
     * 网关地址。
     */
    @SynologyJsonProperty("Gateway")
    private String gateway;
    /**
     * MAC 地址。
     */
    @SynologyJsonProperty("MacAddress")
    private String macAddress;
    /**
     * IP 前缀长度（子网掩码位数），例如 16。
     */
    @SynologyJsonProperty("IPPrefixLen")
    private Integer ipPrefixLen;
    /**
     * 网络端点 ID。
     */
    @SynologyJsonProperty("EndpointID")
    private String endpointId;
    /**
     * 网络 ID。
     */
    @SynologyJsonProperty("NetworkID")
    private String networkId;
}
