package io.github.shuzhuoi.synology.core.system.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Core.System info 方法的响应（通用系统信息）。
 * <p>
 * 注意：不同 DSM 版本返回的字段名存在差异（例如温度有 sys_temp 与 temperature 两种形态，
 * 固件版本有 firmware_ver 与 version 两种形态），两类字段均建模，
 * 未返回的为 null，请以真机响应为准。
 */
@Getter
@Setter
@NoArgsConstructor
public class SystemInfoResponse {

    /**
     * 系统语言代码，例如 enu。
     */
    private String codepage;
    /**
     * 机型，例如 DS920+。
     */
    private String model;
    /**
     * 内存大小，单位 MB。
     */
    @SynologyJsonProperty("ram_size")
    private Long ramSize;
    /**
     * 序列号。
     */
    private String serial;
    /**
     * 系统温度，单位摄氏度（firmware_ver 同代响应使用该字段名）。
     */
    @SynologyJsonProperty("sys_temp")
    private Integer sysTemp;
    /**
     * 系统温度，单位摄氏度（部分 DSM 7 响应使用该字段名）。
     */
    private Integer temperature;
    /**
     * 系统当前时间字符串。
     */
    private String time;
    /**
     * 时区名称。
     */
    @SynologyJsonProperty("time_zone")
    private String timeZone;
    /**
     * 时区描述，例如 GMT+8。
     */
    @SynologyJsonProperty("time_zone_desc")
    private String timeZoneDesc;
    /**
     * 开机时长。不同 DSM 版本可能返回秒数或 "x Days, hh:mm:ss" 字符串。
     */
    @SynologyJsonProperty("up_time")
    private String upTime;
    /**
     * 已连接的 USB 设备列表。
     */
    @SynologyJsonProperty("usb_dev")
    private List<UsbDevice> usbDevices;
    /**
     * 固件版本，例如 DSM 7.2-64570。
     */
    @SynologyJsonProperty("firmware_ver")
    private String firmwareVer;
    /**
     * 固件版本号，部分 DSM 7 响应使用该字段名。
     */
    private String version;
    /**
     * 固件版本可读字符串，部分 DSM 7 响应使用该字段名。
     */
    @SynologyJsonProperty("version_string")
    private String versionString;
    /**
     * CPU 主频，单位 MHz。
     */
    @SynologyJsonProperty("cpu_clock_speed")
    private Integer cpuClockSpeed;
    /**
     * CPU 核心数。
     */
    @SynologyJsonProperty("cpu_cores")
    private String cpuCores;
    /**
     * CPU 家族名称。
     */
    @SynologyJsonProperty("cpu_family")
    private String cpuFamily;
    /**
     * CPU 系列名称。
     */
    @SynologyJsonProperty("cpu_series")
    private String cpuSeries;
    /**
     * 是否启用 NTP 时间同步。
     */
    @SynologyJsonProperty("enabled_ntp")
    private Boolean enabledNtp;
    /**
     * NTP 服务器地址。
     */
    @SynologyJsonProperty("ntp_server")
    private String ntpServer;
}
