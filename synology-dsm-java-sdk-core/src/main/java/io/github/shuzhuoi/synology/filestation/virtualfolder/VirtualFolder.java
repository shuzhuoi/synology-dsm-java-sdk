package io.github.shuzhuoi.synology.filestation.virtualfolder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 虚拟文件系统挂载目录，例如 CIFS、NFS 或 ISO 挂载点。
 */
@Getter
@Setter
@NoArgsConstructor
public class VirtualFolder {

    private String path;
    private String name;
    private Boolean isdir;
    private VirtualFolderAdditional additional;
}
