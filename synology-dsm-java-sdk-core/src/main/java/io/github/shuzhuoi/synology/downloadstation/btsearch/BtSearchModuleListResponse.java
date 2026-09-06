package io.github.shuzhuoi.synology.downloadstation.btsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.Task.BTSearch getModule 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class BtSearchModuleListResponse {

    /**
     * 已启用的搜索引擎模块列表。
     */
    private List<BtSearchModule> modules;
}
