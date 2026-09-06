package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Docker inspect 的 Config 结构（容器基础配置）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerConfig {

    /**
     * 镜像名称，例如 nginx:latest。
     */
    @SynologyJsonProperty("Image")
    private String image;
    /**
     * 主机名。
     */
    @SynologyJsonProperty("Hostname")
    private String hostname;
    /**
     * 域名。
     */
    @SynologyJsonProperty("Domainname")
    private String domainname;
    /**
     * 启动命令。
     */
    @SynologyJsonProperty("Cmd")
    private List<String> cmd;
    /**
     * 入口点。
     */
    @SynologyJsonProperty("Entrypoint")
    private List<String> entrypoint;
    /**
     * 环境变量，形如 KEY=value 的字符串列表。
     */
    @SynologyJsonProperty("Env")
    private List<String> env;
    /**
     * 容器标签。
     */
    @SynologyJsonProperty("Labels")
    private Map<String, String> labels;
    /**
     * 暴露端口声明，键为 "80/tcp" 形式，值通常为空对象。
     */
    @SynologyJsonProperty("ExposedPorts")
    private Map<String, Object> exposedPorts;
    /**
     * 健康检查配置。
     */
    @SynologyJsonProperty("Healthcheck")
    private Map<String, Object> healthcheck;
}
