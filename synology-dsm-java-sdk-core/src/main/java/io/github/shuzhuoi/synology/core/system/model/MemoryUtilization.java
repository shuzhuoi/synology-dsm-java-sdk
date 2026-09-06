package io.github.shuzhuoi.synology.core.system.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 内存实时利用率。
 * <p>
 * 注意：除 real_usage / swap_usage 为百分比外，其余容量字段单位均为 KB。
 */
@Getter
@Setter
@NoArgsConstructor
public class MemoryUtilization {

    /**
     * 设备标识，通常为 "Memory"。
     */
    private String device;
    /**
     * 物理内存使用率，单位百分比。
     */
    @SynologyJsonProperty("real_usage")
    private Integer realUsage;
    /**
     * 物理内存总量，单位 KB。
     */
    @SynologyJsonProperty("memory_size")
    private Long memorySize;
    /**
     * 物理内存可用量，单位 KB。
     */
    @SynologyJsonProperty("avail_real")
    private Long availReal;
    /**
     * 物理内存总量（与 memory_size 同义），单位 KB。
     */
    @SynologyJsonProperty("total_real")
    private Long totalReal;
    /**
     * 交换分区可用量，单位 KB。
     */
    @SynologyJsonProperty("avail_swap")
    private Long availSwap;
    /**
     * 交换分区总量，单位 KB。
     */
    @SynologyJsonProperty("total_swap")
    private Long totalSwap;
    /**
     * 缓冲区占用，单位 KB。
     */
    private Long buffer;
    /**
     * 缓存占用，单位 KB。
     */
    private Long cached;
    /**
     * 交换分区使用率，单位百分比。
     */
    @SynologyJsonProperty("swap_usage")
    private Integer swapUsage;
    /**
     * 每秒换入磁盘的内存量，单位 KB/s。
     */
    @SynologyJsonProperty("si_disk")
    private Long siDisk;
    /**
     * 每秒换出磁盘的内存量，单位 KB/s。
     */
    @SynologyJsonProperty("so_disk")
    private Long soDisk;
}
