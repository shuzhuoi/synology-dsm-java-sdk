package io.github.shuzhuoi.synology.example.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Download Station 官方覆盖示例的本地配置。
 * <p>
 * 该类只服务于 example 模块，用于承载从 downloadstation-coverage.yaml 读取到的运行参数。
 */
@Getter
@Setter
public class DownloadStationCoverageExampleConfig {

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
     * 下载任务的目的地共享目录，例如 /downloads。
     */
    private String destination;

    /**
     * 可选：用于创建下载任务的测试 URL（http/ftp 等）。
     * 留空时跳过任务创建段演示。
     */
    private String testUri;

    /**
     * 可选：本地 .torrent 文件路径，用于演示种子上传创建。
     * 留空时跳过该段演示。
     */
    private String torrentFile;

    /**
     * 可选：BT 搜索关键字，需要在 DSM 启用 BT 搜索引擎。
     * 留空时跳过 BT 搜索段演示。
     */
    private String btSearchKeyword;
}
