package io.github.shuzhuoi.synology.downloadstation.statistic;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 单个下载引擎的当前速度（嵌套于 getInfo 响应）。
 */
@Getter
@Setter
@NoArgsConstructor
public class StatisticSpeed {

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
