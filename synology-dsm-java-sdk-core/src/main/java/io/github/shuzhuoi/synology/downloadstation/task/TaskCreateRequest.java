package io.github.shuzhuoi.synology.downloadstation.task;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.DownloadStation2.Task create 方法的请求参数（URI 形态）。
 * <p>
 * 通过 http/https/ftp/magnet 等 URL 创建下载任务；
 * 需要 .torrent 文件上传时请改用 {@link TaskCreateTorrentRequest}。
 */
@Getter
public class TaskCreateRequest {

    /**
     * 下载链接列表，多值时以逗号分隔下发。
     */
    private final List<String> uris;
    /**
     * 下载目的地路径。
     */
    private final String destination;
    /**
     * 下载完成后自动解压使用的密码（压缩包任务）。
     */
    private final String unzipPassword;
    /**
     * 是否弹出文件选择对话框（种子任务选择部分文件），无界面场景保持 false。
     */
    private final Boolean createList;

    private TaskCreateRequest(Builder builder) {
        this.uris = Collections.unmodifiableList(new ArrayList<String>(builder.uris));
        this.destination = builder.destination;
        this.unzipPassword = builder.unzipPassword;
        this.createList = builder.createList;
    }

    public static Builder builder(String uri) {
        return new Builder(uri);
    }

    public static class Builder {

        private final List<String> uris = new ArrayList<String>();
        private String destination;
        private String unzipPassword;
        private Boolean createList;

        private Builder(String uri) {
            this.uris.add(uri);
        }

        /**
         * 追加一个下载链接。
         */
        public Builder addUri(String uri) {
            this.uris.add(uri);
            return this;
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

        public TaskCreateRequest build() {
            return new TaskCreateRequest(this);
        }
    }
}
