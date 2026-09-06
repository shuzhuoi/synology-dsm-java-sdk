package io.github.shuzhuoi.synology.core.system.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 单块磁盘的实时利用率。
 */
@Getter
@Setter
@NoArgsConstructor
public class DiskDeviceUtilization {

    /**
     * 磁盘设备名，例如 sda。
     */
    private String device;
    /**
     * 展示名，例如 Drive 1。
     */
    @SynologyJsonProperty("display_name")
    private String displayName;
    /**
     * 磁盘类型，例如 internal / esata / usb。
     */
    private String type;
    /**
     * 磁盘繁忙度，单位百分比。
     */
    private Integer utilization;
    /**
     * 读次数统计。
     */
    @SynologyJsonProperty("read_access")
    private Long readAccess;
    /**
     * 写次数统计。
     */
    @SynologyJsonProperty("write_access")
    private Long writeAccess;
    /**
     * 读吞吐，单位 KB/s。
     */
    @SynologyJsonProperty("read_byte")
    private Long readByte;
    /**
     * 写吞吐，单位 KB/s。
     */
    @SynologyJsonProperty("write_byte")
    private Long writeByte;
}
