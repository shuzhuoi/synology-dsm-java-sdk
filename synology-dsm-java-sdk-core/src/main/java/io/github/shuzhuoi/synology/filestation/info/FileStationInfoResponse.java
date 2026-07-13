package io.github.shuzhuoi.synology.filestation.info;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import io.github.shuzhuoi.synology.json.annotation.SynologyJsonStringList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * File Station 基础信息响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class FileStationInfoResponse {

    /**
     * DSM 主机名。
     */
    private String hostname;

    /**
     * 当前登录用户是否为管理员。
     */
    @SynologyJsonProperty("is_manager")
    private Boolean manager;

    /**
     * 当前用户可挂载的虚拟文件系统协议。
     * 官方示例是逗号分隔字符串，部分真实 DSM 会返回数组，因此这里统一转成列表。
     */
    @SynologyJsonProperty("support_virtual_protocol")
    @SynologyJsonStringList
    private List<String> supportVirtualProtocol;

    /**
     * 当前用户是否支持创建文件/文件夹分享链接。
     */
    @SynologyJsonProperty("support_sharing")
    private Boolean supportSharing;
}
