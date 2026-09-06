package io.github.shuzhuoi.synology.downloadstation.info;

import lombok.Getter;

/**
 * SYNO.DownloadStation2.Info setServerConfig 方法的请求参数。
 * <p>
 * 所有字段可空，只有设置了的字段才会下发给 DSM，
 * 未设置的字段保持 DSM 当前值不变。
 */
@Getter
public class DownloadStationConfigRequest {

    /**
     * BT 任务下载限速（KB/s，0 表示不限）。
     */
    private final Integer btMaxDownload;
    /**
     * BT 任务上传限速（KB/s，0 表示不限）。
     */
    private final Integer btMaxUpload;
    /**
     * eMule 任务下载限速（KB/s）。
     */
    private final Integer emuleMaxDownload;
    /**
     * eMule 任务上传限速（KB/s）。
     */
    private final Integer emuleMaxUpload;
    /**
     * NZB 任务下载限速（KB/s）。
     */
    private final Integer nzbMaxDownload;
    /**
     * HTTP 任务下载限速（KB/s）。
     */
    private final Integer httpMaxDownload;
    /**
     * FTP 任务下载限速（KB/s）。
     */
    private final Integer ftpMaxDownload;
    /**
     * 是否启用 eMule 引擎。
     */
    private final Boolean emuleEnabled;
    /**
     * 是否启用下载完成后自动解压。
     */
    private final Boolean unzipServiceEnabled;
    /**
     * 默认下载目的地路径。
     */
    private final String defaultDestination;
    /**
     * eMule 任务默认下载目的地路径。
     */
    private final String emuleDefaultDestination;

    private DownloadStationConfigRequest(Builder builder) {
        this.btMaxDownload = builder.btMaxDownload;
        this.btMaxUpload = builder.btMaxUpload;
        this.emuleMaxDownload = builder.emuleMaxDownload;
        this.emuleMaxUpload = builder.emuleMaxUpload;
        this.nzbMaxDownload = builder.nzbMaxDownload;
        this.httpMaxDownload = builder.httpMaxDownload;
        this.ftpMaxDownload = builder.ftpMaxDownload;
        this.emuleEnabled = builder.emuleEnabled;
        this.unzipServiceEnabled = builder.unzipServiceEnabled;
        this.defaultDestination = builder.defaultDestination;
        this.emuleDefaultDestination = builder.emuleDefaultDestination;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer btMaxDownload;
        private Integer btMaxUpload;
        private Integer emuleMaxDownload;
        private Integer emuleMaxUpload;
        private Integer nzbMaxDownload;
        private Integer httpMaxDownload;
        private Integer ftpMaxDownload;
        private Boolean emuleEnabled;
        private Boolean unzipServiceEnabled;
        private String defaultDestination;
        private String emuleDefaultDestination;

        /**
         * 设置 BT 任务下载限速（KB/s，0 表示不限）。
         */
        public Builder btMaxDownload(Integer btMaxDownload) {
            this.btMaxDownload = btMaxDownload;
            return this;
        }

        /**
         * 设置 BT 任务上传限速（KB/s，0 表示不限）。
         */
        public Builder btMaxUpload(Integer btMaxUpload) {
            this.btMaxUpload = btMaxUpload;
            return this;
        }

        /**
         * 设置 eMule 任务下载限速（KB/s）。
         */
        public Builder emuleMaxDownload(Integer emuleMaxDownload) {
            this.emuleMaxDownload = emuleMaxDownload;
            return this;
        }

        /**
         * 设置 eMule 任务上传限速（KB/s）。
         */
        public Builder emuleMaxUpload(Integer emuleMaxUpload) {
            this.emuleMaxUpload = emuleMaxUpload;
            return this;
        }

        /**
         * 设置 NZB 任务下载限速（KB/s）。
         */
        public Builder nzbMaxDownload(Integer nzbMaxDownload) {
            this.nzbMaxDownload = nzbMaxDownload;
            return this;
        }

        /**
         * 设置 HTTP 任务下载限速（KB/s）。
         */
        public Builder httpMaxDownload(Integer httpMaxDownload) {
            this.httpMaxDownload = httpMaxDownload;
            return this;
        }

        /**
         * 设置 FTP 任务下载限速（KB/s）。
         */
        public Builder ftpMaxDownload(Integer ftpMaxDownload) {
            this.ftpMaxDownload = ftpMaxDownload;
            return this;
        }

        /**
         * 设置是否启用 eMule 引擎。
         */
        public Builder emuleEnabled(Boolean emuleEnabled) {
            this.emuleEnabled = emuleEnabled;
            return this;
        }

        /**
         * 设置是否启用下载完成后自动解压。
         */
        public Builder unzipServiceEnabled(Boolean unzipServiceEnabled) {
            this.unzipServiceEnabled = unzipServiceEnabled;
            return this;
        }

        /**
         * 设置默认下载目的地路径。
         */
        public Builder defaultDestination(String defaultDestination) {
            this.defaultDestination = defaultDestination;
            return this;
        }

        /**
         * 设置 eMule 任务默认下载目的地路径。
         */
        public Builder emuleDefaultDestination(String emuleDefaultDestination) {
            this.emuleDefaultDestination = emuleDefaultDestination;
            return this;
        }

        public DownloadStationConfigRequest build() {
            return new DownloadStationConfigRequest(this);
        }
    }
}
