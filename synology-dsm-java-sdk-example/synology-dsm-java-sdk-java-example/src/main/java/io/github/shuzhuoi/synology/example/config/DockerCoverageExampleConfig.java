package io.github.shuzhuoi.synology.example.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Docker 官方契约覆盖示例的本地配置。
 * <p>
 * 该类只服务于 example 模块，用于承载从 docker-coverage.yaml 读取到的运行参数。
 */
@Getter
@Setter
public class DockerCoverageExampleConfig {

    /**
     * DSM 服务地址，例如：https://nas.example.com:5001。
     */
    private String dsmUrl;

    /**
     * DSM 登录账号。注意：SYNO.Docker.* 通常需要管理员或具有 Container Manager 权限的账户。
     */
    private String account;

    /**
     * DSM 登录密码。真实文件 docker-coverage.yaml 已被 .gitignore 忽略，请不要提交。
     */
    private String password;

    /**
     * 可选：详情与日志演练的目标容器名称。
     * 留空（默认）跳过详情和日志段演示。
     */
    private String containerName;

    /**
     * 可选：生命周期演练项，支持 start / stop / restart。
     * 留空（默认）不执行任何生命周期操作；填写后示例会真实启停目标容器（需同时配置 containerName）。
     */
    private String lifecycleAction;
}
