package io.github.shuzhuoi.synology.downloadstation.task;

import lombok.Getter;

import java.io.File;

/**
 * SYNO.DownloadStation2.Task create 方法的请求参数（种子文件形态）。
 * <p>
 * 通过上传本地 .torrent 文件创建下载任务，走 multipart/form-data。
 */
@Getter
public class TaskCreateTorrentRequest {

    /**
     * 本地种子文件。
     */
    private final File torrentFile;
    /**
     * 下载目的地路径。
     */
    private final String destination;
    /**
     * 下载完成后自动解压使用的密码（压缩包任务）。
     */
    private final String unzipPassword;
    /**
     * 是否弹出文件选择对话框（选择部分种子内容），无界面场景保持 false。
     */
    private final Boolean createList;

    private TaskCreateTorrentRequest(Builder builder) {
        this.torrentFile = builder.torrentFile;
        this.destination = builder.destination;
        this.unzipPassword = builder.unzipPassword;
        this.createList = builder.createList;
    }

    public static Builder builder(File torrentFile) {
        return new Builder(torrentFile);
    }

    public static class Builder {

        private final File torrentFile;
        private String destination;
        private String unzipPassword;
        private Boolean createList;

        private Builder(File torrentFile) {
            this.torrentFile = torrentFile;
        }

        /**
         * 设置下载目的地路径。
         */
        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        /**
         * 设置下载完成后自动解压使用的密码。
         */
        public Builder unzipPassword(String unzipPassword) {
            this.unzipPassword = unzipPassword;
            return this;
        }

        /**
         * 设置是否弹出文件选择对话框。
         */
        public Builder createList(Boolean createList) {
            this.createList = createList;
            return this;
        }

        public TaskCreateTorrentRequest build() {
            if (torrentFile == null) {
                throw new IllegalArgumentException("torrentFile must not be null");
            }
            if (!torrentFile.exists() || !torrentFile.isFile()) {
                throw new IllegalArgumentException("torrentFile must be an existing file");
            }
            return new TaskCreateTorrentRequest(this);
        }
    }
}
