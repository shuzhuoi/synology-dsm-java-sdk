package io.github.shuzhuoi.synology.downloadstation.btsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BT 搜索结果条目。
 */
@Getter
@Setter
@NoArgsConstructor
public class BtSearchItem {

    /**
     * 种子标题。
     */
    private String title;
    /**
     * 种子下载链接（magnet 或 torrent URL）。
     */
    private String download;
    /**
     * 种子大小（字节）。
     */
    private Long size;
    /**
     * 发布日期。
     */
    private String date;
    /**
     * 当前 peer 数。
     */
    private Integer peers;
    /**
     * 当前做种数。
     */
    private Integer seeds;
    /**
     * 当前下载数。
     */
    private Integer leechs;
    /**
     * 种子分类。
     */
    private String category;
}
