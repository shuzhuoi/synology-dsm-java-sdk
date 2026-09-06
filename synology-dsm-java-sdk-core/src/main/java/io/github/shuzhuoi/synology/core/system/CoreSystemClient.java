package io.github.shuzhuoi.synology.core.system;

import io.github.shuzhuoi.synology.core.system.info.CoreSystemInfoClient;
import io.github.shuzhuoi.synology.core.system.utilization.CoreSystemUtilizationClient;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

/**
 * DSM Core System API 聚合入口。
 * <p>
 * 该类不直接发请求，只负责把系统监控能力按资源类型拆分到不同客户端中。
 */
public class CoreSystemClient {

    /**
     * 系统信息与电源控制接口。
     */
    private final CoreSystemInfoClient infoClient;
    /**
     * CPU / 内存 / 磁盘 / 网络实时利用率接口。
     */
    private final CoreSystemUtilizationClient utilizationClient;

    public CoreSystemClient(SynologyApiExecutor executor) {
        this.infoClient = new CoreSystemInfoClient(executor);
        this.utilizationClient = new CoreSystemUtilizationClient(executor);
    }

    public CoreSystemInfoClient info() {
        return infoClient;
    }

    public CoreSystemUtilizationClient utilization() {
        return utilizationClient;
    }
}
