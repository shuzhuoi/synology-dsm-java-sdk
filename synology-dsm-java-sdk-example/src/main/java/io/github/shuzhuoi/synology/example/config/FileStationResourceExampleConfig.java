package io.github.shuzhuoi.synology.example.config;

import lombok.Getter;
import lombok.Setter;

/**
 * File Station 资源能力示例配置。
 */
@Getter
@Setter
public class FileStationResourceExampleConfig {

    /**
     * DSM 服务地址，例如：https://nas.example.com:5001。
     */
    private String dsmUrl;

    /**
     * DSM 登录账号。
     */
    private String account;

    /**
     * DSM 登录密码。
     */
    private String password;

    /**
     * File Station 中用于演示资源能力的远端目录。
     */
    private String sampleFolder;

    /**
     * 缩略图保存到本地的目录。
     */
    private String downloadFolder;

    /**
     * 虚拟目录类型，例如 cifs、nfs 或 iso。
     */
    private String virtualFolderType;

    /**
     * 分享链接演示用密码，最长 16 位。
     */
    private String sharingPassword;
}
