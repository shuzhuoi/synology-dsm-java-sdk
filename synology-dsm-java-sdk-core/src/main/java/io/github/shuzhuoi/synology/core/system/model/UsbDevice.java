package io.github.shuzhuoi.synology.core.system.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 系统信息中的 USB 设备。
 */
@Getter
@Setter
@NoArgsConstructor
public class UsbDevice {

    /**
     * 设备分类。
     */
    @SynologyJsonProperty("class")
    private String deviceClass;
    /**
     * 产品名。
     */
    private String product;
    /**
     * 厂商名。
     */
    private String vendor;
}
