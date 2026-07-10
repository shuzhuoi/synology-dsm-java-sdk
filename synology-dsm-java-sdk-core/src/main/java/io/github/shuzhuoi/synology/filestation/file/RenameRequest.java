package io.github.shuzhuoi.synology.filestation.file;

import io.github.shuzhuoi.synology.model.Additional;
import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RenameRequest {

    /**
     * 要重命名的文件/文件夹路径列表。
     */
    private final List<String> paths;
    /**
     * 新名称列表，必须与 paths 一一对应，只传文件名，不传完整路径。
     */
    private final List<String> names;
    /**
     * 需要额外返回的文件属性，例如 size、owner、time、perm。
     */
    private final List<Additional> additional;
    /**
     * Search start 返回的任务 ID，用于让 DSM 同步更新搜索结果。
     */
    private final String searchTaskId;

    private RenameRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String path : builder.paths) {
            normalized.add(SynologyPath.normalize(path));
        }
        this.paths = Collections.unmodifiableList(normalized);
        this.names = Collections.unmodifiableList(new ArrayList<String>(builder.names));
        this.additional = Collections.unmodifiableList(new ArrayList<Additional>(builder.additional));
        this.searchTaskId = builder.searchTaskId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> paths = new ArrayList<String>();
        private List<String> names = new ArrayList<String>();
        private List<Additional> additional = new ArrayList<Additional>();
        private String searchTaskId;

        public Builder addRename(String path, String name) {
            this.paths.add(path);
            this.names.add(name);
            return this;
        }

        public Builder addAdditional(Additional additional) {
            if (additional != null) {
                this.additional.add(additional);
            }
            return this;
        }

        public Builder searchTaskId(String searchTaskId) {
            this.searchTaskId = searchTaskId;
            return this;
        }

        public RenameRequest build() {
            if (paths.isEmpty() || paths.size() != names.size()) {
                throw new IllegalArgumentException("paths and names must be paired");
            }
            return new RenameRequest(this);
        }
    }
}
