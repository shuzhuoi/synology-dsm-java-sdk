package io.github.shuzhuoi.synology.downloadstation.btsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BT 搜索引擎模块。
 */
@Getter
@Setter
@NoArgsConstructor
public class BtSearchModule {

    /**
     * 模块内部名，例如 all、piratebay。
     */
    private String name;
    /**
     * 模块显示名。
     */
    private String displayname;
}
