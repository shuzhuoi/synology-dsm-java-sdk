package io.github.shuzhuoi.synology.downloadstation.rss;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RSS 订阅站点实体。
 */
@Getter
@Setter
@NoArgsConstructor
public class RssSite {

    /**
     * 站点 ID。
     */
    private String id;
    /**
     * 站点标题。
     */
    private String title;
    /**
     * 站点 RSS 地址。
     */
    private String url;
    /**
     * 是否正在更新中。
     */
    @SynologyJsonProperty("is_updating")
    private Boolean updating;
    /**
     * 上次更新时间（Unix 时间戳，秒）。
     */
    @SynologyJsonProperty("last_update")
    private Long lastUpdate;
}
