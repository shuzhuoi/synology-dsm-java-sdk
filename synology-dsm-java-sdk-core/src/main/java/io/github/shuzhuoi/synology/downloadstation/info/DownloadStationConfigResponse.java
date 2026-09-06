package io.github.shuzhuoi.synology.downloadstation.info;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.DownloadStation2.Info getConfig 方法的响应。
 * <p>
 * 各协议限速单位为 KB/s，0 表示不限速。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadStationConfigResponse {

    /**
     * BT 任务下载限速（KB/s，0 表示不限）。
     */
    @SynologyJsonProperty("bt_max_download")
    private Integer btMaxDownload;
    /**
     * BT 任务上传限速（KB/s，0 表示不限）。
     */
    @SynologyJsonProperty("bt_max_upload")
    private Integer btMaxUpload;
    /**
     * eMule 任务下载限速（KB/s）。
     */
    @SynologyJsonProperty("emule_max_download")
    private Integer emuleMaxDownload;
    /**
     * eMule 任务上传限速（KB/s）。
     */
    @SynologyJsonProperty("emule_max_upload")
    private Integer emuleMaxUpload;
    /**
     * NZB 任务下载限速（KB/s）。
     */
    @SynologyJsonProperty("nzb_max_download")
    private Integer nzbMaxDownload;
    /**
     * HTTP 任务下载限速（KB/s）。
     */
    @SynologyJsonProperty("http_max_download")
    private Integer httpMaxDownload;
    /**
     * FTP 任务下载限速（KB/s）。
     */
    @SynologyJsonProperty("ftp_max_download")
    private Integer ftpMaxDownload;
    /**
     * 是否启用 eMule 引擎。
     */
    @SynologyJsonProperty("emule_enabled")
    private Boolean emuleEnabled;
    /**
     * 是否启用下载完成后自动解压。
     */
    @SynologyJsonProperty("unzip_service_enabled")
    private Boolean unzipServiceEnabled;
    /**
     * 默认下载目的地路径。
     */
    @SynologyJsonProperty("default_destination")
    private String defaultDestination;
    /**
     * eMule 任务默认下载目的地路径。
     */
    @SynologyJsonProperty("emule_default_destination")
    private String emuleDefaultDestination;
}
