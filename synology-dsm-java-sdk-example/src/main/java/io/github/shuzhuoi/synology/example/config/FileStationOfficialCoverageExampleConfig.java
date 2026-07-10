package io.github.shuzhuoi.synology.example.config;

import lombok.Getter;
import lombok.Setter;

/**
 * File Station 官方契约补齐示例的本地配置。
 * <p>
 * 该类只服务于 example 模块，用于承载从 filestation-official-coverage.yaml 读取到的运行参数。
 */
@Getter
@Setter
public class FileStationOfficialCoverageExampleConfig {

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
     * File Station 中用于演示官方补齐接口的远端目录。
     * <p>
     * 示例会在该目录下创建临时子目录并在结束时清理，请确保指向专用测试目录。
     */
    private String sampleFolder;
}
