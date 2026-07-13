package io.github.shuzhuoi.synology.filestation.compress;

/**
 * DSM 压缩更新模式。
 */
public enum CompressMode {
    ADD("add"),
    UPDATE("update"),
    REFRESHEN("refreshen"),
    SYNCHRONIZE("synchronize");

    private final String value;

    CompressMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
