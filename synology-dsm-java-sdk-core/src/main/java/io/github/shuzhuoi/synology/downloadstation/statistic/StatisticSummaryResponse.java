package io.github.shuzhuoi.synology.downloadstation.statistic;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.DownloadStation2.Statistic getStatistic 方法的响应。
 * <p>
 * 返回自 Download Station 启用以来的累计上下行流量。
 */
@Getter
@Setter
@NoArgsConstructor
public class StatisticSummaryResponse {

    /**
     * 主引擎累计下载字节数。
     */
    @SynologyJsonProperty("size_download")
    private Long sizeDownload;
    /**
     * 主引擎累计上传字节数。
     */
    @SynologyJsonProperty("size_upload")
    private Long sizeUpload;
    /**
     * eMule 引擎累计流量。
     */
    private StatisticSize emule;
    /**
     * NZB 引擎累计流量。
     */
    private StatisticSize nzb;
}
