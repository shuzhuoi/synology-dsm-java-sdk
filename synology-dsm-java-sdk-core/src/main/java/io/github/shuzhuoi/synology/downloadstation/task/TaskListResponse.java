package io.github.shuzhuoi.synology.downloadstation.task;

import io.github.shuzhuoi.synology.downloadstation.model.DownloadTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.Task list 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskListResponse {

    /**
     * 任务总数。
     */
    private Integer total;
    /**
     * 当前页起始序号。
     */
    private Integer offset;
    /**
     * 任务列表。
     */
    private List<DownloadTask> tasks;
}
