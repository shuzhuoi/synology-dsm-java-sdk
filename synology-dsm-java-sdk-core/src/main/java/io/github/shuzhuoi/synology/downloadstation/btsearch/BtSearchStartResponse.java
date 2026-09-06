package io.github.shuzhuoi.synology.downloadstation.btsearch;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.DownloadStation2.Task.BTSearch start 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class BtSearchStartResponse {

    /**
     * 搜索任务 ID，后续 list/clean 使用该 ID。
     */
    @SynologyJsonProperty("taskid")
    private String taskid;
}
