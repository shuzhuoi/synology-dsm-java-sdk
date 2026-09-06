package io.github.shuzhuoi.synology.downloadstation.model;

/**
 * 任务附加信息字段枚举。
 * <p>
 * list/getinfo 的 additional 参数按需指定，DSM 只返回请求过的扩展块。
 */
public enum TaskAdditionalField {

    /**
     * 任务详情（目的地、URI、创建时间、连接数等）。
     */
    DETAIL("detail"),
    /**
     * 传输统计（已下载/已上传字节、上下行速度）。
     */
    TRANSFER("transfer"),
    /**
     * 任务内文件列表。
     */
    FILE("file"),
    /**
     * tracker 列表（BT 任务）。
     */
    TRACKER("tracker"),
    /**
     * peer 列表（BT 任务）。
     */
    PEER("peer");

    private final String value;

    TaskAdditionalField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
