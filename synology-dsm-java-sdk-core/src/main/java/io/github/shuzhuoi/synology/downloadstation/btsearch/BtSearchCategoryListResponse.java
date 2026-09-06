package io.github.shuzhuoi.synology.downloadstation.btsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.Task.BTSearch getCategory 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class BtSearchCategoryListResponse {

    /**
     * 可用的搜索分类列表。
     */
    private List<String> categories;
}
