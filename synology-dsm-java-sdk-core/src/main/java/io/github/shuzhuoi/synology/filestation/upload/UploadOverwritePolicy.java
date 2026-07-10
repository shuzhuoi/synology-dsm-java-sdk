package io.github.shuzhuoi.synology.filestation.upload;

/**
 * DSM 7 / Upload API v3 的上传覆盖策略。
 */
public enum UploadOverwritePolicy {
    /**
     * 目标已存在同名文件时覆盖。
     */
    OVERWRITE("overwrite"),
    /**
     * 目标已存在同名文件时跳过上传。
     */
    SKIP("skip");

    private final String value;

    UploadOverwritePolicy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
