package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Docker inspect 的 HostConfig 结构（宿主机相关配置）。
 * <p>
 * 端口映射（PortBindings）是最常用的字段：键为容器端口声明（如 "80/tcp"），
 * 值为宿主机端口绑定列表。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerHostConfig {

    /**
     * 端口映射：容器端口声明 -> 宿主机绑定列表。
     */
    @SynologyJsonProperty("PortBindings")
    private Map<String, List<DockerPortBinding>> portBindings;
    /**
     * 重启策略，例如 {"Name":"always"}。
     */
    @SynologyJsonProperty("RestartPolicy")
    private Map<String, Object> restartPolicy;
    /**
     * 网络模式，例如 bridge / host。
     */
    @SynologyJsonProperty("NetworkMode")
    private String networkMode;
    /**
     * 绑定的宿主机数据卷，形如 "source:destination" 的字符串列表。
     */
    @SynologyJsonProperty("Binds")
    private List<String> binds;
}
