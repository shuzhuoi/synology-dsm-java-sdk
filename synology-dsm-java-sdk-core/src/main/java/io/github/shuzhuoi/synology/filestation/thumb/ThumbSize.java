package io.github.shuzhuoi.synology.filestation.thumb;

/**
 * DSM 缩略图尺寸。
 */
public enum ThumbSize {
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
    ORIGINAL("original");

    private final String value;

    ThumbSize(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
