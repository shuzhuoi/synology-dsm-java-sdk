package io.github.shuzhuoi.synology.downloadstation.model;

/**
 * 下载任务状态枚举。
 * <p>
 * DSM 返回的 status 为字符串，未收录的值可通过 {@link #fromValue(String)} 容忍解析，
 * 不会因为 DSM 新增状态导致反序列化失败。
 */
public enum DownloadTaskStatus {

    /**
     * 等待下载。
     */
    WAITING("waiting"),
    /**
     * 正在下载。
     */
    DOWNLOADING("downloading"),
    /**
     * 已暂停。
     */
    PAUSED("paused"),
    /**
     * 即将完成（收尾中）。
     */
    FINISHING("finishing"),
    /**
     * 已完成。
     */
    FINISHED("finished"),
    /**
     * 哈希校验中。
     */
    HASH_CHECKING("hash_checking"),
    /**
     * 做种中（BT 任务完成后继续上传）。
     */
    SEEDING("seeding"),
    /**
     * 等待网盘解析。
     */
    FILEHOSTING_WAITING("filehosting_waiting"),
    /**
     * 解压中。
     */
    EXTRACTING("extracting"),
    /**
     * 任务出错。
     */
    ERROR("error");

    private final String value;

    DownloadTaskStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 按官方字符串值解析，未知值返回 null 而不抛异常。
     */
    public static DownloadTaskStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DownloadTaskStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
