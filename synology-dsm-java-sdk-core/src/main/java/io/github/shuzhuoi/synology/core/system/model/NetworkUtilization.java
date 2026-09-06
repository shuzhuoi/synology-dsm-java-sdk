package io.github.shuzhuoi.synology.core.system.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 单个网络接口的实时吞吐。
 */
@Getter
@Setter
@NoArgsConstructor
public class NetworkUtilization {

    /**
     * 网络接口名，例如 eth0；汇总行固定为 total。
     */
    private String device;
    /**
     * 接收速度，单位 KB/s。
     */
    private Long rx;
    /**
     * 发送速度，单位 KB/s。
     */
    private Long tx;
}
