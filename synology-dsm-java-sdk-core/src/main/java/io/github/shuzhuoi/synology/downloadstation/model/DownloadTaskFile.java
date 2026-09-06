package io.github.shuzhuoi.synology.downloadstation.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 任务内的单个文件。
 * <p>
 * 既出现在 additional.file 扩展块，也是 Task.File get 接口的响应元素。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadTaskFile {

    /**
     * 文件名。
     */
    private String filename;
    /**
     * 文件在任务内的序号。
     */
    private Integer index;
    /**
     * 文件总字节数。
     */
    private Long size;
    /**
     * 已下载字节数。
     */
    @SynologyJsonProperty("size_downloaded")
    private Long sizeDownloaded;
    /**
     * 是否被选中下载（BT 任务可选择部分文件）。
     */
    private Boolean selected;
}
