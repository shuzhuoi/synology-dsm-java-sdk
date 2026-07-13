package io.github.shuzhuoi.synology.filestation.virtualfolder;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import io.github.shuzhuoi.synology.filestation.model.SynologyOwner;
import io.github.shuzhuoi.synology.filestation.model.SynologyPermission;
import io.github.shuzhuoi.synology.filestation.model.SynologyTime;
import io.github.shuzhuoi.synology.filestation.model.SynologyVolumeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 虚拟目录挂载点扩展信息。
 */
@Getter
@Setter
@NoArgsConstructor
public class VirtualFolderAdditional {

    @SynologyJsonProperty("real_path")
    private String realPath;
    private SynologyOwner owner;
    private SynologyTime time;
    private SynologyPermission perm;
    @SynologyJsonProperty("mount_point_type")
    private String mountPointType;
    @SynologyJsonProperty("volume_status")
    private SynologyVolumeStatus volumeStatus;
}
