package io.github.shuzhuoi.synology.filestation.favorite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DSM File Station 收藏夹项。
 */
@Getter
@Setter
@NoArgsConstructor
public class Favorite {

    /**
     * 收藏目标路径，必须从共享目录开始。
     */
    private String path;
    /**
     * 收藏名称。
     */
    private String name;
    /**
     * 收藏状态：valid 或 broken。
     */
    private String status;
    /**
     * 是否为目录。字段名沿用 DSM 返回的 isdir。
     */
    private Boolean isdir;
    private FavoriteAdditional additional;
}
