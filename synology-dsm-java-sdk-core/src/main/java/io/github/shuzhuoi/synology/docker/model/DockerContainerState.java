package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Docker inspect 的 State 结构，描述容器运行状态。
 * <p>
 * 字段名与 Docker 原生 inspect 保持一致（大写开头），通过注解映射。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerState {

    /**
     * 容器是否正在运行。
     */
    @SynologyJsonProperty("Running")
    private Boolean running;
    /**
     * 状态可读字符串，例如 running / exited。
     */
    @SynologyJsonProperty("Status")
    private String status;
    /**
     * 是否暂停。
     */
    @SynologyJsonProperty("Paused")
    private Boolean paused;
    /**
     * 是否重启中。
     */
    @SynologyJsonProperty("Restarting")
    private Boolean restarting;
    /**
     * 是否因内存不足被终止（OOM）。
     */
    @SynologyJsonProperty("OOMKilled")
    private Boolean oomKilled;
    /**
     * 是否处于 Dead 状态。
     */
    @SynologyJsonProperty("Dead")
    private Boolean dead;
    /**
     * 主进程 PID。
     */
    @SynologyJsonProperty("Pid")
    private Integer pid;
    /**
     * 退出码，仅在容器已退出时有意义。
     */
    @SynologyJsonProperty("ExitCode")
    private Integer exitCode;
    /**
     * 错误信息。
     */
    @SynologyJsonProperty("Error")
    private String error;
    /**
     * 启动时间，ISO 8601 字符串。
     */
    @SynologyJsonProperty("StartedAt")
    private String startedAt;
    /**
     * 结束时间，ISO 8601 字符串。
     */
    @SynologyJsonProperty("FinishedAt")
    private String finishedAt;
}
