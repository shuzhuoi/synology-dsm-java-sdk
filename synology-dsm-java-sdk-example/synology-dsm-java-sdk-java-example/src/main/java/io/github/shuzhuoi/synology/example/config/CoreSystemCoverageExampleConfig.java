package io.github.shuzhuoi.synology.example.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Core System 官方契约覆盖示例的本地配置。
 * <p>
 * 该类只服务于 example 模块，用于承载从 coresystem-coverage.yaml 读取到的运行参数。
 */
@Getter
@Setter
public class CoreSystemCoverageExampleConfig {

    /**
     * DSM 服务地址，例如：https://nas.example.com:5001。
     */
    private String dsmUrl;

    /**
     * DSM 登录账号。
     */
    private String account;

    /**
     * DSM 登录密码。真实文件 coresystem-coverage.yaml 已被 .gitignore 忽略，请不要提交。
     */
    private String password;

    /**
     * 可选：电源操作演练项，支持 shutdown / reboot。
     * 留空（默认）不执行任何电源操作；填写后示例会真实关机或重启目标 NAS，请谨慎使用。
     */
    private String powerAction;
}
