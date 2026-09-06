package io.github.shuzhuoi.synology.downloadstation.rss;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.RSS.Site list 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class RssSiteListResponse {

    /**
     * 站点总数。
     */
    private Integer total;
    /**
     * 当前页起始序号。
     */
    private Integer offset;
    /**
     * 站点列表。
     */
    private List<RssSite> sites;
}
