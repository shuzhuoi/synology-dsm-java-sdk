package io.github.shuzhuoi.synology.core.system.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CPU 实时利用率。
 */
@Getter
@Setter
@NoArgsConstructor
public class CpuUtilization {

    /**
     * 设备标识，通常为 "System"。
     */
    private String device;
    /**
     * 用户态 CPU 占比，单位百分比。
     */
    @SynologyJsonProperty("user_load")
    private Integer userLoad;
    /**
     * 内核态 CPU 占比，单位百分比。
     */
    @SynologyJsonProperty("system_load")
    private Integer systemLoad;
    /**
     * 其他（如 IO 等待）CPU 占比，单位百分比。
     */
    @SynologyJsonProperty("other_load")
    private Integer otherLoad;
    /**
     * 最近 1 分钟平均负载。
     */
    @SynologyJsonProperty("1min_load")
    private Integer oneMinLoad;
    /**
     * 最近 5 分钟平均负载。
     */
    @SynologyJsonProperty("5min_load")
    private Integer fiveMinLoad;
    /**
     * 最近 15 分钟平均负载。
     */
    @SynologyJsonProperty("15min_load")
    private Integer fifteenMinLoad;
}
