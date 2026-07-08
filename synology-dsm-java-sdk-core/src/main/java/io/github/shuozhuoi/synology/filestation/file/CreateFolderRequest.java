package io.github.shuzhuoi.synology.filestation.file;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class CreateFolderRequest {

    /**
     * 父目录路径列表，必须与 names 一一对应。
     */
    private final List<String> folderPaths;
    /**
     * 要创建的目录名称列表，必须与 folderPaths 一一对应。
     */
    private final List<String> names;
    /**
     * 父目录不存在时是否自动创建父目录。
     */
    private final Boolean forceParent;

    private CreateFolderRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String folderPath : builder.folderPaths) {
            normalized.add(SynologyPath.normalize(folderPath));
        }
        this.folderPaths = Collections.unmodifiableList(normalized);
        this.names = Collections.unmodifiableList(new ArrayList<String>(builder.names));
        this.forceParent = builder.forceParent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> folderPaths = new ArrayList<String>();
        private List<String> names = new ArrayList<String>();
        private Boolean forceParent = Boolean.TRUE;

        public Builder addFolder(String folderPath, String name) {
            this.folderPaths.add(folderPath);
            this.names.add(name);
            return this;
        }

        public Builder forceParent(Boolean forceParent) {
            this.forceParent = forceParent;
            return this;
        }

        public CreateFolderRequest build() {
            if (folderPaths.isEmpty() || folderPaths.size() != names.size()) {
                throw new IllegalArgumentException("folderPaths and names must be paired");
            }
            return new CreateFolderRequest(this);
        }
    }
}
