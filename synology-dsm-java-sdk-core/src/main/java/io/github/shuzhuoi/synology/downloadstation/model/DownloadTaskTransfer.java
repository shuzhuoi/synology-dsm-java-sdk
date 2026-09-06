package io.github.shuzhuoi.synology.downloadstation.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 任务传输统计（additional.transfer）。
 * <p>
 * 字节与速度均使用 Long 承载，避免大文件场景溢出。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadTaskTransfer {

    /**
     * 已下载字节数。
     */
    @SynologyJsonProperty("size_downloaded")
    private Long sizeDownloaded;
    /**
     * 已上传字节数。
     */
    @SynologyJsonProperty("size_uploaded")
    private Long sizeUploaded;
    /**
     * 当前下载速度（字节/秒）。
     */
    @SynologyJsonProperty("speed_download")
    private Long speedDownload;
    /**
     * 当前上传速度（字节/秒）。
     */
    @SynologyJsonProperty("speed_upload")
    private Long speedUpload;
}
