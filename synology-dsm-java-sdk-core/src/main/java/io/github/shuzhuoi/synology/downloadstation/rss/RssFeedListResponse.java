package io.github.shuzhuoi.synology.downloadstation.rss;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.RSS.Feed list 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class RssFeedListResponse {

    /**
     * 条目总数。
     */
    private Integer total;
    /**
     * 当前页起始序号。
     */
    private Integer offset;
    /**
     * 订阅条目列表。
     */
    private List<RssFeed> feeds;
}
