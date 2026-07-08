package io.github.shuzhuoi.synology.filestation.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
    /**
     * MD5 任务完成后的文件摘要。非 MD5 任务通常为空。
     */
    private String md5;
}
