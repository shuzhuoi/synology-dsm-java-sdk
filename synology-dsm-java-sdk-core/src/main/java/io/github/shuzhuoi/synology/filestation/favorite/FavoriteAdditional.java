package io.github.shuzhuoi.synology.filestation.favorite;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import io.github.shuzhuoi.synology.filestation.model.SynologyOwner;
import io.github.shuzhuoi.synology.filestation.model.SynologyPermission;
import io.github.shuzhuoi.synology.filestation.model.SynologyTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 收藏夹目标目录的扩展信息。
 */
@Getter
@Setter
@NoArgsConstructor
public class FavoriteAdditional {

    @SynologyJsonProperty("real_path")
    private String realPath;
    private SynologyOwner owner;
    private SynologyTime time;
    private SynologyPermission perm;
    @SynologyJsonProperty("mount_point_type")
    private String mountPointType;
    /**
     * 文件扩展名或类型，只有请求 additional=type 时返回。
     */
    private String type;
}
