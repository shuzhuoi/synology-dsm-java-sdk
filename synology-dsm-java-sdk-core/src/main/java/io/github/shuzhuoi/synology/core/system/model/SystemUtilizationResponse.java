package io.github.shuzhuoi.synology.core.system.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Core.System.Utilization get 方法的响应（CPU / 内存 / 磁盘 / 网络实时利用率）。
 * <p>
 * 一次调用返回全部资源数据，未包含的分区（如无交换分区）为 null。
 */
@Getter
@Setter
@NoArgsConstructor
public class SystemUtilizationResponse {

    /**
     * CPU 利用率与平均负载。
     */
    private CpuUtilization cpu;
    /**
     * 内存利用率。
     */
    private MemoryUtilization memory;
    /**
     * 各磁盘利用率。
     */
    private DiskUtilization disk;
    /**
     * 各网络接口吞吐列表，包含 device 为 total 的汇总行。
     */
    private List<NetworkUtilization> network;
}
