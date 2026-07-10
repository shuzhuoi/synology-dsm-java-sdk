package io.github.shuzhuoi.synology.filestation.virtualfolder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirtualFolderAdditional {

    @JsonProperty("real_path")
    private String realPath;
    private SynologyOwner owner;
    private SynologyTime time;
    private SynologyPermission perm;
    @JsonProperty("mount_point_type")
    private String mountPointType;
    @JsonProperty("volume_status")
    private SynologyVolumeStatus volumeStatus;
}
