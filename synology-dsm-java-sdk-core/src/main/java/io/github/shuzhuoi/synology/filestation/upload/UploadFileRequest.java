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
     * 目标文件已存在时是否覆盖，兼容 Upload API v2 的 true/false 表达。
     */
    private final Boolean overwrite;
    /**
     * DSM 7 / Upload API v3 的字符串覆盖策略。设置后优先于 overwrite(Boolean)。
     */
    private final UploadOverwritePolicy overwritePolicy;
    /**
     * 设置上传文件的最后修改时间，单位：Linux timestamp 毫秒。
     */
    private final Long mtime;
    /**
     * 设置上传文件的创建时间，单位：Linux timestamp 毫秒。
     */
    private final Long crtime;
    /**
     * 设置上传文件的最后访问时间，单位：Linux timestamp 毫秒。
     */
    private final Long atime;

    private UploadFileRequest(Builder builder) {
        this.path = SynologyPath.normalize(builder.path);
        this.file = builder.file;
        this.createParents = builder.createParents;
        this.overwrite = builder.overwrite;
        this.overwritePolicy = builder.overwritePolicy;
        this.mtime = builder.mtime;
        this.crtime = builder.crtime;
        this.atime = builder.atime;
    }

    public static Builder builder(String path, File file) {
        return new Builder(path, file);
    }

    public boolean useUploadApiV3() {
        return overwritePolicy != null;
    }

    public String overwriteParameter() {
        if (overwritePolicy != null) {
            return overwritePolicy.getValue();
        }
        return overwrite == null ? null : String.valueOf(overwrite);
    }

    public static class Builder {
        private final String path;
        private final File file;
        private Boolean createParents = Boolean.TRUE;
        private Boolean overwrite = Boolean.TRUE;
        private UploadOverwritePolicy overwritePolicy;
        private Long mtime;
        private Long crtime;
        private Long atime;

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
            this.overwritePolicy = null;
            return this;
        }

        public Builder overwritePolicy(UploadOverwritePolicy overwritePolicy) {
            this.overwritePolicy = overwritePolicy;
            return this;
        }

        public Builder mtime(Long mtime) {
            this.mtime = mtime;
            return this;
        }

        public Builder crtime(Long crtime) {
            this.crtime = crtime;
            return this;
        }

        public Builder atime(Long atime) {
            this.atime = atime;
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
