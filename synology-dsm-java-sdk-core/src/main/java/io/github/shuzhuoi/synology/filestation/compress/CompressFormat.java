package io.github.shuzhuoi.synology.filestation.compress;

/**
 * DSM 压缩格式。
 */
public enum CompressFormat {
    ZIP("zip"),
    SEVEN_Z("7z");

    private final String value;

    CompressFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
