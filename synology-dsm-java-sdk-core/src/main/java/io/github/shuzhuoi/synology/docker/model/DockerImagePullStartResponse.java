package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.Docker.Image pull_start 方法的响应（发起镜像拉取）。
 * <p>
 * 拉取是异步操作：pull_start 返回 task_id 后，
 * 需携带该 task_id 轮询 pull_status 直到拉取结束。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerImagePullStartResponse {

    /**
     * 拉取任务 ID，用于后续 pull_status 轮询。
     */
    @SynologyJsonProperty("task_id")
    private String taskId;
}
