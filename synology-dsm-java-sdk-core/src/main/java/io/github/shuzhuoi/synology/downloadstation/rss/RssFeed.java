package io.github.shuzhuoi.synology.downloadstation.rss;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RSS 订阅条目实体。
 */
@Getter
@Setter
@NoArgsConstructor
public class RssFeed {

    /**
     * 条目 ID。
     */
    private String id;
    /**
     * 条目标题。
     */
    private String title;
    /**
     * 条目页面地址。
     */
    private String url;
    /**
     * 可直接创建下载任务的链接（magnet 或 torrent）。
     */
    @SynologyJsonProperty("download_uri")
    private String downloadUri;
    /**
     * 发布时间（Unix 时间戳，秒）。
     */
    @SynologyJsonProperty("publish_date")
    private Long publishDate;
}
