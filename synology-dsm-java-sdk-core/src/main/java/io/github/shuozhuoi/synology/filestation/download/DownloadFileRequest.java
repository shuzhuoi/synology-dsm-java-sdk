package io.github.shuzhuoi.synology.filestation.download;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class DownloadFileRequest {

    /**
     * 要下载的 DSM 文件/文件夹路径。多个路径时 DSM 会返回 zip 内容。
     */
    private final List<String> paths;
    /**
     * 下载模式，download 通常触发附件下载语义，open 通常用于直接打开。
     */
    private final DownloadMode mode;

    private DownloadFileRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String path : builder.paths) {
            normalized.add(SynologyPath.normalize(path));
        }
        this.paths = Collections.unmodifiableList(normalized);
        this.mode = builder.mode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> paths = new ArrayList<String>();
        private DownloadMode mode = DownloadMode.DOWNLOAD;

        public Builder addPath(String path) {
            this.paths.add(path);
            return this;
        }

        public Builder mode(DownloadMode mode) {
            this.mode = mode;
            return this;
        }

        public DownloadFileRequest build() {
            if (paths.isEmpty()) {
                throw new IllegalArgumentException("at least one path is required");
            }
            return new DownloadFileRequest(this);
        }
    }
}
