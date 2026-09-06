package io.github.shuzhuoi.synology.core.system.utilization;

import io.github.shuzhuoi.synology.core.system.CoreSystemApi;
import io.github.shuzhuoi.synology.core.system.model.SystemUtilizationResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

import java.util.Collections;

/**
 * SYNO.Core.System.Utilization 客户端。
 * <p>
 * 提供资源监控器中的实时数据，是构建监控面板的核心数据源。
 */
public class CoreSystemUtilizationClient {

    private final SynologyApiExecutor executor;

    public CoreSystemUtilizationClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询 CPU / 内存 / 磁盘 / 网络的实时利用率，一次调用返回全部资源数据。
     *
     * @return 实时利用率汇总
     */
    public SystemUtilizationResponse get() {
        // get 无业务参数，返回与 DSM 资源监控器一致的实时快照。
        return executor.getAuthenticated(
                "entry.cgi",
                CoreSystemApi.UTILIZATION_API,
                CoreSystemApi.UTILIZATION_VERSION,
                "get",
                Collections.<String, String>emptyMap(),
                SystemUtilizationResponse.class
        );
    }
}
