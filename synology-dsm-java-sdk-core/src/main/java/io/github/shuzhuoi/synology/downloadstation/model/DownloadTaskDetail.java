package io.github.shuzhuoi.synology.downloadstation.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 任务详情（additional.detail）。
 * <p>
 * 注意：DSM 返回的字段名为下划线风格，通过 SDK 中立注解显式映射到驼峰属性。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadTaskDetail {

    /**
     * 任务下载目的地路径。
     */
    private String destination;
    /**
     * 任务来源链接（magnet、http、ftp 等）。
     */
    private String uri;
    /**
     * 任务创建时间（Unix 时间戳，秒）。
     */
    @SynologyJsonProperty("create_time")
    private Long createTime;
    /**
     * 任务优先级：auto、low、normal、high 等。
     */
    private String priority;
    /**
     * 已连接的做种者数量（BT 任务）。
     */
    @SynologyJsonProperty("connected_seeders")
    private Integer connectedSeeders;
    /**
     * 已连接的下载者数量（BT 任务）。
     */
    @SynologyJsonProperty("connected_leechers")
    private Integer connectedLeechers;
    /**
     * 种子总分片数（BT 任务）。
     */
    @SynologyJsonProperty("total_pieces")
    private Long totalPieces;
}
