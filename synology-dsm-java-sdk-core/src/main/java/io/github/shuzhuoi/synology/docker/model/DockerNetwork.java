package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Network list 返回的单个 Docker 网络信息。
 * <p>
 * 字段名以真机响应为准（全小写下划线风格，注意 iprange 无下划线）。
 * remove 方法需要把本对象整体序列化为 JSON 数组参数传回 DSM。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerNetwork {

    /**
     * 网络 ID，64 位十六进制字符串。
     */
    private String id;
    /**
     * 网络名称，例如 bridge、host、vault_default。
     */
    private String name;
    /**
     * 网络驱动，例如 bridge / host / none。
     */
    private String driver;
    /**
     * 子网 CIDR，例如 172.22.0.0/16。
     */
    private String subnet;
    /**
     * 网关 IP，例如 172.22.0.1。
     */
    private String gateway;
    /**
     * 可分配 IP 范围，未配置时为空字符串。
     */
    private String iprange;
    /**
     * 是否启用 IPv6。
     */
    @SynologyJsonProperty("enable_ipv6")
    private Boolean enableIpv6;
    /**
     * 接入该网络的容器名称列表。
     */
    private List<String> containers;
}
