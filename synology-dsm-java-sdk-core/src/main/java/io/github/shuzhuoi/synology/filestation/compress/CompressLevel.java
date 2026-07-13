package io.github.shuzhuoi.synology.filestation.compress;

/**
 * DSM 压缩等级。
 */
public enum CompressLevel {
    MODERATE("moderate"),
    STORE("store"),
    FASTEST("fastest"),
    BEST("best");

    private final String value;

    CompressLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
