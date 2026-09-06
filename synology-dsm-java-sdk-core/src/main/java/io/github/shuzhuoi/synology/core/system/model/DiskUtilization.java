package io.github.shuzhuoi.synology.core.system.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 磁盘实时利用率汇总（官方响应中磁盘列表嵌套在 "disk" 节点下）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DiskUtilization {

    /**
     * 各磁盘的利用率明细。
     */
    @SynologyJsonProperty("disk")
    private List<DiskDeviceUtilization> disks;
}
