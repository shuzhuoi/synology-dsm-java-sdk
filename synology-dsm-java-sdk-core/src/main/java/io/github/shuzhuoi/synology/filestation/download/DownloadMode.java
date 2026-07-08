package io.github.shuzhuoi.synology.filestation.download;

public enum DownloadMode {
    OPEN("open"),
    DOWNLOAD("download");

    private final String value;

    DownloadMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
