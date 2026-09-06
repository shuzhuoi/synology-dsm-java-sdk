package io.github.shuzhuoi.synology.downloadstation.task;

import io.github.shuzhuoi.synology.downloadstation.model.DownloadTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.Task getinfo 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskGetInfoResponse {

    /**
     * 任务详情列表。
     */
    private List<DownloadTask> tasks;
}
