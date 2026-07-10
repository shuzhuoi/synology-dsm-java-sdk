package io.github.shuzhuoi.synology.filestation.file;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class DeleteRequest {

    /**
     * 要删除的文件/文件夹路径列表。
     */
    private final List<String> paths;
    /**
     * 非阻塞删除时是否精确计算进度。精确计算更准但可能更慢。
     */
    private final Boolean accurateProgress;
    /**
     * 是否递归删除文件夹内的内容。为空时按 DSM 默认 true 处理。
     */
    private final Boolean recursive;
    /**
     * Search start 返回的任务 ID，用于让 DSM 同步更新搜索结果。
     */
    private final String searchTaskId;

    private DeleteRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String path : builder.paths) {
            normalized.add(SynologyPath.normalize(path));
        }
        this.paths = Collections.unmodifiableList(normalized);
        this.accurateProgress = builder.accurateProgress;
        this.recursive = builder.recursive;
        this.searchTaskId = builder.searchTaskId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> paths = new ArrayList<String>();
        private Boolean accurateProgress;
        private Boolean recursive;
        private String searchTaskId;

        public Builder addPath(String path) {
            this.paths.add(path);
            return this;
        }

        public Builder accurateProgress(Boolean accurateProgress) {
            this.accurateProgress = accurateProgress;
            return this;
        }

        public Builder recursive(Boolean recursive) {
            this.recursive = recursive;
            return this;
        }

        public Builder searchTaskId(String searchTaskId) {
            this.searchTaskId = searchTaskId;
            return this;
        }

        public DeleteRequest build() {
            if (paths.isEmpty()) {
                throw new IllegalArgumentException("at least one path is required");
            }
            return new DeleteRequest(this);
        }
    }
}
