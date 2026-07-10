package io.github.shuzhuoi.synology.filestation.permission;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

@Getter
public class CheckPermissionWriteRequest {

    /**
     * 需要检查写入权限的目标目录路径，必须从共享目录开始。
     */
    private final String path;
    /**
     * 准备写入的文件名，只传文件名，不传完整路径。
     */
    private final String filename;
    /**
     * 目标文件已存在时是否允许覆盖。为空时按 DSM 默认规则处理，可能返回已存在错误。
     */
    private final Boolean overwrite;
    /**
     * 是否只检查创建新文件/文件夹的权限。官方默认 true。
     */
    private final Boolean createOnly;

    private CheckPermissionWriteRequest(Builder builder) {
        this.path = SynologyPath.normalize(builder.path);
        this.filename = builder.filename;
        this.overwrite = builder.overwrite;
        this.createOnly = builder.createOnly;
    }

    public static Builder builder(String path, String filename) {
        return new Builder(path, filename);
    }

    public static class Builder {
        private final String path;
        private final String filename;
        private Boolean overwrite;
        private Boolean createOnly = Boolean.TRUE;

        private Builder(String path, String filename) {
            this.path = path;
            this.filename = filename;
        }

        public Builder overwrite(Boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public Builder createOnly(Boolean createOnly) {
            this.createOnly = createOnly;
            return this;
        }

        public CheckPermissionWriteRequest build() {
            if (filename == null || filename.trim().length() == 0) {
                throw new IllegalArgumentException("filename must not be blank");
            }
            return new CheckPermissionWriteRequest(this);
        }
    }
}
