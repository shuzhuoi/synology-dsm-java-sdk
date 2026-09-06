package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.Docker.Image pull_status 方法的响应（查询镜像拉取进度）。
 * <p>
 * 官方未公开该响应示例，字段依据 Synology 异步任务的通用形态推定
 * （status 表示任务状态，error 在失败时携带错误信息），
 * 做宽松映射（未知字段忽略），真机验证后如有出入只需补充字段。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerImagePullStatusResponse {

    /**
     * 拉取任务 ID。
     */
    @SynologyJsonProperty("task_id")
    private String taskId;
    /**
     * 任务状态，例如 processing / finished / error。
     */
    private String status;
    /**
     * 拉取进度信息，具体格式（百分比或文本）以真机响应为准。
     */
    private Object progress;
    /**
     * 失败时的错误信息。
     */
    private Object error;
}
