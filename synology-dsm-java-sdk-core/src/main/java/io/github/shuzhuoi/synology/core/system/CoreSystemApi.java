package io.github.shuzhuoi.synology.core.system;

/**
 * DSM Core System API 名称与版本常量。
 * <p>
 * 系统信息与实时资源利用率属于 DSM 核心能力，无需安装额外套件。
 * 注意：shutdown / reboot 方法仅存在于 SYNO.Core.System 的 v1 契约，
 * info 使用 v3（社区文档 DSM 7 推定）。请以
 * {@code CoreSystemCoverageExample} 启动时打印的 SYNO.API.Info 探测结果为准，
 * 如有偏差只需修正本类中的常量，无需改动各客户端。
 */
public final class CoreSystemApi {

    /**
     * 系统信息与电源控制 API。
     */
    public static final String SYSTEM_API = "SYNO.Core.System";
    /**
     * info 方法使用的版本。
     */
    public static final int SYSTEM_INFO_VERSION = 3;
    /**
     * shutdown / reboot 方法使用的版本（官方契约中电源操作仅 v1 提供）。
     */
    public static final int SYSTEM_POWER_VERSION = 1;

    /**
     * 系统资源实时利用率 API（CPU / 内存 / 磁盘 / 网络）。
     */
    public static final String UTILIZATION_API = "SYNO.Core.System.Utilization";
    /**
     * Utilization API 版本。
     */
    public static final int UTILIZATION_VERSION = 1;

    private CoreSystemApi() {
    }
}
