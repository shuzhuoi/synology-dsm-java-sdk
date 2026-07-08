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

    private DeleteRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String path : builder.paths) {
            normalized.add(SynologyPath.normalize(path));
        }
        this.paths = Collections.unmodifiableList(normalized);
        this.accurateProgress = builder.accurateProgress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> paths = new ArrayList<String>();
        private Boolean accurateProgress;

        public Builder addPath(String path) {
            this.paths.add(path);
            return this;
        }

        public Builder accurateProgress(Boolean accurateProgress) {
            this.accurateProgress = accurateProgress;
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
