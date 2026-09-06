package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * SYNO.Docker.Container get（inspect 详情）返回的容器完整配置。
 * <p>
 * 与 Docker 原生 inspect 结构一致（字段大写开头），是获取端口映射等
 * 完整信息的唯一途径（list 响应不含 port_bindings）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerDetail {

    /**
     * 容器名称，Docker inspect 格式带前导斜杠，例如 "/nginx"。
     */
    @SynologyJsonProperty("Name")
    private String name;
    /**
     * 创建时间，ISO 8601 字符串。
     */
    @SynologyJsonProperty("Created")
    private String created;
    /**
     * 容器基础配置（镜像、命令、环境变量等）。
     */
    @SynologyJsonProperty("Config")
    private DockerContainerConfig config;
    /**
     * 宿主机相关配置（端口映射等）。
     */
    @SynologyJsonProperty("HostConfig")
    private DockerContainerHostConfig hostConfig;
    /**
     * 运行状态。
     */
    @SynologyJsonProperty("State")
    private DockerContainerState state;
    /**
     * 网络配置摘要。
     */
    @SynologyJsonProperty("NetworkSettings")
    private DockerContainerNetworkSettings networkSettings;
    /**
     * 容器标签。
     */
    @SynologyJsonProperty("Labels")
    private Map<String, String> labels;
    /**
     * AppArmor 安全配置名称。
     */
    @SynologyJsonProperty("AppArmorProfile")
    private String appArmorProfile;
    /**
     * 启动参数。
     */
    @SynologyJsonProperty("Args")
    private List<String> args;
}
