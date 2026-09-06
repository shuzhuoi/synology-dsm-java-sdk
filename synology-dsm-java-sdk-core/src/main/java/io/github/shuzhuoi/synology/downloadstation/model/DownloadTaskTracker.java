package io.github.shuzhuoi.synology.downloadstation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BT 任务的 tracker 信息（additional.tracker 元素）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadTaskTracker {

    /**
     * tracker 服务器地址。
     */
    private String url;
    /**
     * tracker 状态。
     */
    private String status;
    /**
     * 当前 tracker 可见的 peer 数。
     */
    private Integer peers;
    /**
     * 当前 tracker 可见的做种数。
     */
    private Integer seeds;
    /**
     * 当前 tracker 可见的下载数。
     */
    private Integer leechs;
}
