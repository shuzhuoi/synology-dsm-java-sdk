package io.github.shuzhuoi.synology.example.config;

import lombok.Getter;
import lombok.Setter;

/**
 * File Station 归档能力示例配置。
 * <p>
 * 该类只服务于 example 模块，用于承载从 filestation-archive.yaml 读取到的运行参数。
 */
@Getter
@Setter
public class FileStationArchiveExampleConfig {

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
     * File Station 中用于创建本次运行临时子目录的远端根目录。
     */
    private String sampleFolder;
}
