package io.github.shuzhuoi.synology.filestation.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskStatusResponse {

    /**
     * 任务是否完成。
     */
    private Boolean finished;
    /**
     * 任务进度，官方文档中通常是 0 到 1 的小数。
     */
    private Double progress;
    /**
     * DSM 返回的任务状态文本。
     */
    private String status;
}
