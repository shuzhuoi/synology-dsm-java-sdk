package io.github.shuzhuoi.synology.filestation.file;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class CopyMoveRequest {

    /**
     * 要复制或移动的源路径列表。
     */
    private final List<String> paths;
    /**
     * 目标目录路径。
     */
    private final String destFolderPath;
    /**
     * 目标存在同名文件时是否覆盖。为空时按 DSM 默认规则处理，可能返回错误。
     */
    private final Boolean overwrite;
    /**
     * 是否精确计算复制/移动进度。
     */
    private final Boolean accurateProgress;
    /**
     * Search start 返回的任务 ID，用于让 DSM 同步更新搜索结果。
     */
    private final String searchTaskId;

    private CopyMoveRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String path : builder.paths) {
            normalized.add(SynologyPath.normalize(path));
        }
        this.paths = Collections.unmodifiableList(normalized);
        this.destFolderPath = SynologyPath.normalize(builder.destFolderPath);
        this.overwrite = builder.overwrite;
        this.accurateProgress = builder.accurateProgress;
        this.searchTaskId = builder.searchTaskId;
    }

    public static Builder builder(String destFolderPath) {
        return new Builder(destFolderPath);
    }

    public static class Builder {
        private List<String> paths = new ArrayList<String>();
        private final String destFolderPath;
        private Boolean overwrite;
        private Boolean accurateProgress;
        private String searchTaskId;

        private Builder(String destFolderPath) {
            this.destFolderPath = destFolderPath;
        }

        public Builder addPath(String path) {
            this.paths.add(path);
            return this;
        }

        public Builder overwrite(Boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public Builder accurateProgress(Boolean accurateProgress) {
            this.accurateProgress = accurateProgress;
            return this;
        }

        public Builder searchTaskId(String searchTaskId) {
            this.searchTaskId = searchTaskId;
            return this;
        }

        public CopyMoveRequest build() {
            if (paths.isEmpty()) {
                throw new IllegalArgumentException("at least one path is required");
            }
            return new CopyMoveRequest(this);
        }
    }
}
