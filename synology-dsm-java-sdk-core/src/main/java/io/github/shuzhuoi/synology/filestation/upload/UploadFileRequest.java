package io.github.shuzhuoi.synology.filestation.upload;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.io.File;

@Getter
public class UploadFileRequest {

    /**
     * DSM 目标目录路径，必须从共享目录开始。
     */
    private final String path;
    /**
     * 本地待上传文件。
     */
    private final File file;
    /**
     * 目标父目录不存在时是否自动创建。
     */
    private final Boolean createParents;
    /**
     * 目标文件已存在时是否覆盖。
     */
    private final Boolean overwrite;

    private UploadFileRequest(Builder builder) {
        this.path = SynologyPath.normalize(builder.path);
        this.file = builder.file;
        this.createParents = builder.createParents;
        this.overwrite = builder.overwrite;
    }

    public static Builder builder(String path, File file) {
        return new Builder(path, file);
    }

    public static class Builder {
        private final String path;
        private final File file;
        private Boolean createParents = Boolean.TRUE;
        private Boolean overwrite = Boolean.TRUE;

        private Builder(String path, File file) {
            this.path = path;
            this.file = file;
        }

        public Builder createParents(Boolean createParents) {
            this.createParents = createParents;
            return this;
        }

        public Builder overwrite(Boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public UploadFileRequest build() {
            if (file == null) {
                throw new IllegalArgumentException("file must not be null");
            }
            if (!file.exists() || !file.isFile()) {
                throw new IllegalArgumentException("file must be an existing file");
            }
            return new UploadFileRequest(this);
        }
    }
}
