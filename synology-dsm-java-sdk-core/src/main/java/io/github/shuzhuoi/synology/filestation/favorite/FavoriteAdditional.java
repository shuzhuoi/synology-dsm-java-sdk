package io.github.shuzhuoi.synology.filestation.favorite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteAdditional {

    @JsonProperty("real_path")
    private String realPath;
    private SynologyOwner owner;
    private SynologyTime time;
    private SynologyPermission perm;
    @JsonProperty("mount_point_type")
    private String mountPointType;
    /**
     * 文件扩展名或类型，只有请求 additional=type 时返回。
     */
    private String type;
}
