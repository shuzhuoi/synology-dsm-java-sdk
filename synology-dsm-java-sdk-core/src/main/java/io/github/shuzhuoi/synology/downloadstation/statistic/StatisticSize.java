package io.github.shuzhuoi.synology.downloadstation.statistic;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 单个下载引擎的累计流量（嵌套于 getStatistic 响应）。
 */
@Getter
@Setter
@NoArgsConstructor
public class StatisticSize {

    /**
     * 累计下载字节数。
     */
    @SynologyJsonProperty("size_download")
    private Long sizeDownload;
    /**
     * 累计上传字节数。
     */
    @SynologyJsonProperty("size_upload")
    private Long sizeUpload;
}
