package io.github.shuzhuoi.synology.example.config;

import lombok.Getter;
import lombok.Setter;

/**
 * File Station v0.2.0 高级示例的本地配置。
 * <p>
 * 该类只服务于 example 模块，用于承载从 filestation-advanced.yaml 读取到的运行参数。
 * 与基础示例相比，本示例不依赖本地文件上传，测试数据由程序在远端临时生成。
 */
@Getter
@Setter
public class FileStationAdvancedExampleConfig {

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
     * File Station 中用于演示 v0.2.0 功能的远端目录。
     * <p>
     * 该目录下的测试文件会在示例结束时被清理，请确保指向专用测试目录。
     */
    private String sampleFolder;
}
