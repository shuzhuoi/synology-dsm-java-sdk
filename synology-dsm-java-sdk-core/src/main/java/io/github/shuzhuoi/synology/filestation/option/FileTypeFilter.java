package io.github.shuzhuoi.synology.filestation.option;

/**
 * DSM 文件类型过滤条件。
 */
public enum FileTypeFilter {
    FILE("file"),
    DIR("dir"),
    ALL("all");

    private final String value;

    FileTypeFilter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
