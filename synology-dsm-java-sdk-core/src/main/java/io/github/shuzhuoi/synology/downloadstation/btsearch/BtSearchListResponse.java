package io.github.shuzhuoi.synology.downloadstation.btsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.Task.BTSearch list 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class BtSearchListResponse {

    /**
     * 搜索结果总数。
     */
    private Integer total;
    /**
     * 当前页起始序号。
     */
    private Integer offset;
    /**
     * 搜索引擎是否已返回全部结果。
     */
    private Boolean finished;
    /**
     * 搜索结果条目。
     */
    private List<BtSearchItem> items;
}
