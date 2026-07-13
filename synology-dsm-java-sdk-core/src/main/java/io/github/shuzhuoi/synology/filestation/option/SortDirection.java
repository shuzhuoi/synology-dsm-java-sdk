package io.github.shuzhuoi.synology.filestation.option;

/**
 * DSM 列表接口的排序方向。
 */
public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private final String value;

    SortDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
