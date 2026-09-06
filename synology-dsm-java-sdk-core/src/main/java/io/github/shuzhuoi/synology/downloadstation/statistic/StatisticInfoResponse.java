package io.github.shuzhuoi.synology.downloadstation.statistic;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.DownloadStation2.Statistic getInfo 方法的响应。
 * <p>
 * 返回主引擎（BT/HTTP/FTP/NZB）与 eMule、NZB 引擎的当前上下行速度。
 * 注意：eMule/NZB 嵌套结构为官方文档推定，若实测为平铺字段只需调整本类映射。
 */
@Getter
@Setter
@NoArgsConstructor
public class StatisticInfoResponse {

    /**
     * 主引擎当前下载速度（字节/秒）。
     */
    @SynologyJsonProperty("speed_download")
    private Long speedDownload;
    /**
     * 主引擎当前上传速度（字节/秒）。
     */
    @SynologyJsonProperty("speed_upload")
    private Long speedUpload;
    /**
     * eMule 引擎速度。
     */
    private StatisticSpeed emule;
    /**
     * NZB 引擎速度。
     */
    private StatisticSpeed nzb;
}
