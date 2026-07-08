package io.github.shuzhuoi.synology.filestation.file;

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

    private RenameRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String path : builder.paths) {
            normalized.add(SynologyPath.normalize(path));
        }
        this.paths = Collections.unmodifiableList(normalized);
        this.names = Collections.unmodifiableList(new ArrayList<String>(builder.names));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> paths = new ArrayList<String>();
        private List<String> names = new ArrayList<String>();

        public Builder addRename(String path, String name) {
            this.paths.add(path);
            this.names.add(name);
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
