package io.github.shuzhuoi.synology.downloadstation.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BT 任务的 peer 信息（additional.peer 元素）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadTaskPeer {

    /**
     * 对端地址（IP:端口）。
     */
    private String address;
    /**
     * 对端 BT 客户端标识。
     */
    private String agent;
    /**
     * 该 peer 的任务进度（0-100）。
     */
    private Integer progress;
    /**
     * 从该 peer 的下载速度（字节/秒）。
     */
    @SynologyJsonProperty("speed_download")
    private Long speedDownload;
    /**
     * 向该 peer 的上传速度（字节/秒）。
     */
    @SynologyJsonProperty("speed_upload")
    private Long speedUpload;
}
