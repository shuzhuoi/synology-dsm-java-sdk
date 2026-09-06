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

    /**
     * 可选：详情演练的目标镜像仓库名，例如 nginx、grafana/grafana。
     * 留空（默认）跳过镜像详情段演示。
     */
    private String imageName;

    /**
     * 可选：拉取演练的镜像仓库，例如 nginx。
     * 留空（默认）跳过镜像拉取段演示；填写后示例会真实从仓库拉取镜像（占用带宽与磁盘）。
     */
    private String pullRepository;

    /**
     * 可选：拉取演练的镜像标签，默认 latest。
     */
    private String pullTag;

    /**
     * 可选：详情演练的目标 Compose 项目名称（Container Manager 中显示的名称）。
     * 留空（默认）跳过项目详情段演示。
     */
    private String projectName;

    /**
     * 可选：项目生命周期演练项，支持 start / stop / restart / clean / delete。
     * 危险操作：clean 会停止并移除项目容器，delete 会删除整个项目！
     * 留空（默认）不执行任何项目生命周期操作；填写后示例会真实操作目标项目（需同时配置 projectName）。
     */
    private String projectAction;
}
