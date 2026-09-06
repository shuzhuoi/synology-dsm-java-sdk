package io.github.shuzhuoi.synology.core.system.info;

import io.github.shuzhuoi.synology.core.system.CoreSystemApi;
import io.github.shuzhuoi.synology.core.system.model.SystemInfoResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.Core.System 客户端。
 * <p>
 * 提供系统通用信息查询以及关机、重启等电源操作。
 * 电源操作属于高危动作，调用前请确认目标 NAS。
 */
public class CoreSystemInfoClient {

    private final SynologyApiExecutor executor;

    public CoreSystemInfoClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询系统通用信息（机型、内存、序列号、温度、固件版本、开机时长等）。
     *
     * @return 系统信息
     */
    public SystemInfoResponse getInfo() {
        // info 无业务参数，返回通用系统信息；不同 DSM 版本返回的字段名略有差异，见响应类注释。
        return executor.getAuthenticated(
                "entry.cgi",
                CoreSystemApi.SYSTEM_API,
                CoreSystemApi.SYSTEM_INFO_VERSION,
                "info",
                Collections.<String, String>emptyMap(),
                SystemInfoResponse.class
        );
    }

    /**
     * 关闭 NAS。这是不可逆的高危操作，请谨慎调用。
     *
     * @return 操作结果
     */
    public SynologyOperationResponse shutdown() {
        // 官方契约中电源操作仅存在于 v1，local=true 表示本机执行。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("local", SynologyParameterEncoder.booleanValue(Boolean.TRUE));
        executor.getAuthenticated(
                "entry.cgi",
                CoreSystemApi.SYSTEM_API,
                CoreSystemApi.SYSTEM_POWER_VERSION,
                "shutdown",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }

    /**
     * 重启 NAS。正在运行的任务会被中断，请谨慎调用。
     *
     * @return 操作结果
     */
    public SynologyOperationResponse reboot() {
        // 官方契约中电源操作仅存在于 v1。
        executor.getAuthenticated(
                "entry.cgi",
                CoreSystemApi.SYSTEM_API,
                CoreSystemApi.SYSTEM_POWER_VERSION,
                "reboot",
                Collections.<String, String>emptyMap(),
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
