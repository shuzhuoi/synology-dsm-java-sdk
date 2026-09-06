package io.github.shuzhuoi.synology.downloadstation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 任务 additional 扩展块聚合。
 * <p>
 * 只有在 list/getinfo 请求中声明了对应的 additional 字段时，
 * DSM 才会填充对应子对象，未请求的字段为 null。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadTaskAdditional {

    /**
     * 任务详情（目的地、URI、创建时间等）。
     */
    private DownloadTaskDetail detail;
    /**
     * 传输统计（已下载/已上传字节、上下行速度）。
     */
    private DownloadTaskTransfer transfer;
    /**
     * 任务内文件列表。
     */
    private List<DownloadTaskFile> file;
    /**
     * tracker 列表（BT 任务）。
     */
    private List<DownloadTaskTracker> tracker;
    /**
     * peer 列表（BT 任务）。
     */
    private List<DownloadTaskPeer> peer;
}
