package io.github.shuzhuoi.synology.filestation.backgroundtask;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.FileStation.BackgroundTask list 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class BackgroundTaskListResponse {

    /**
     * 后台任务总数。
     */
    private Integer total;
    /**
     * 当前返回结果的偏移量。
     */
    private Integer offset;
    /**
     * 后台任务列表。
     */
    private List<BackgroundTask> tasks;
}
