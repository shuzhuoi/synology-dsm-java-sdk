package io.github.shuzhuoi.synology.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.api.ApiInfoResponse;
import io.github.shuzhuoi.synology.api.SynologyApiDescriptor;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.core.system.model.CpuUtilization;
import io.github.shuzhuoi.synology.core.system.model.DiskDeviceUtilization;
import io.github.shuzhuoi.synology.core.system.model.MemoryUtilization;
import io.github.shuzhuoi.synology.core.system.model.NetworkUtilization;
import io.github.shuzhuoi.synology.core.system.model.SystemInfoResponse;
import io.github.shuzhuoi.synology.core.system.model.SystemUtilizationResponse;
import io.github.shuzhuoi.synology.example.config.CoreSystemCoverageExampleConfig;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Core System 官方契约覆盖示例。
 * <p>
 * 演示 SYNO.Core.System / SYNO.Core.System.Utilization 的全部能力：
 * 系统信息（机型、固件、温度、开机时长）、实时利用率（CPU / 内存 / 磁盘 / 网络），
 * 以及可选的电源操作演练（shutdown / reboot，默认跳过）。
 * <p>
 * 运行前请复制 classpath 下的 coresystem-coverage.example.yaml 为
 * coresystem-coverage.yaml，并填写真实 DSM 地址、账号和密码。
 * 示例默认只做只读查询；powerAction 留空时不执行任何电源操作。
 */
@Slf4j
public class CoreSystemCoverageExample {

    private static final String CONFIG_FILE = "coresystem-coverage.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "coresystem-coverage.example.yaml";
    private static final String POWER_ACTION_SHUTDOWN = "shutdown";
    private static final String POWER_ACTION_REBOOT = "reboot";

    public static void main(String[] args) throws IOException {
        CoreSystemCoverageExampleConfig sampleConfig = readSampleConfig();
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(requiredConfigValue(sampleConfig.getDsmUrl(), "dsmUrl"))
                .account(requiredConfigValue(sampleConfig.getAccount(), "account"))
                .password(requiredConfigValue(sampleConfig.getPassword(), "password"))
                .autoRefreshSession(Boolean.TRUE)
                .build();

        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(config, new JacksonSynologyJsonCodec());

        try {
            // 触发登录并复用 SID。
            client.session().currentSession();
            probeApiVersions(client);
            showSystemInfo(client);
            showUtilization(client);
            runPowerActionIfConfigured(client, sampleConfig.getPowerAction());
            log.info("Core System 官方覆盖示例执行完成");
        } finally {
            client.session().logout();
        }
    }

    /**
     * 演示系统信息查询：机型、内存、序列号、温度、固件版本、开机时长、USB 设备。
     * <p>
     * 不同 DSM 版本的字段名存在差异（如 sys_temp 与 temperature、firmware_ver 与 version），
     * 两种形态均打印，便于真机核对实际返回的字段。
     */
    private static void showSystemInfo(SynologyDsmClient client) {
        SystemInfoResponse info = client.coreSystem().info().getInfo();
        log.info("系统信息：机型={}，内存={}MB，序列号={}，开机时长={}",
                info.getModel(), info.getRamSize(), info.getSerial(), info.getUpTime());
        log.info("固件版本：firmwareVer={}，version={}，versionString={}",
                info.getFirmwareVer(), info.getVersion(), info.getVersionString());
        log.info("系统温度：sysTemp={}，temperature={}，CPU={} x {} 核 @ {}MHz",
                info.getSysTemp(), info.getTemperature(),
                info.getCpuFamily(), info.getCpuCores(), info.getCpuClockSpeed());
        log.info("时间：{}（{} {}），NTP 启用={}，服务器={}，USB 设备数={}",
                info.getTime(), info.getTimeZone(), info.getTimeZoneDesc(),
                info.getEnabledNtp(), info.getNtpServer(),
                info.getUsbDevices() == null ? 0 : info.getUsbDevices().size());
    }

    /**
     * 演示实时利用率查询：CPU 负载、内存占用、各磁盘繁忙度、各网卡吞吐。
     * <p>
     * 一次 get 调用返回全部资源数据，等价于 DSM 资源监控器首页。
     */
    private static void showUtilization(SynologyDsmClient client) {
        SystemUtilizationResponse utilization = client.coreSystem().utilization().get();

        CpuUtilization cpu = utilization.getCpu();
        if (cpu != null) {
            int totalLoad = nz(cpu.getUserLoad()) + nz(cpu.getSystemLoad()) + nz(cpu.getOtherLoad());
            log.info("CPU：用户={}%，系统={}%，其他={}%，合计={}%，负载 1/5/15 分钟={}/{}/{}",
                    cpu.getUserLoad(), cpu.getSystemLoad(), cpu.getOtherLoad(), totalLoad,
                    cpu.getOneMinLoad(), cpu.getFiveMinLoad(), cpu.getFifteenMinLoad());
        }

        MemoryUtilization memory = utilization.getMemory();
        if (memory != null) {
            log.info("内存：使用率={}%，总量={}KB，可用={}KB，缓存={}KB，交换分区使用率={}%",
                    memory.getRealUsage(), memory.getTotalReal(), memory.getAvailReal(),
                    memory.getCached(), memory.getSwapUsage());
        }

        if (utilization.getDisk() != null && utilization.getDisk().getDisks() != null) {
            for (DiskDeviceUtilization disk : utilization.getDisk().getDisks()) {
                log.info("磁盘：{}（{}）繁忙度={}%，读={}KB/s，写={}KB/s",
                        disk.getDisplayName(), disk.getDevice(),
                        disk.getUtilization(), disk.getReadByte(), disk.getWriteByte());
            }
        }

        List<NetworkUtilization> networks = utilization.getNetwork();
        if (networks != null) {
            for (NetworkUtilization network : networks) {
                log.info("网络：{} 接收={}KB/s，发送={}KB/s", network.getDevice(), network.getRx(), network.getTx());
            }
        }
    }

    /**
     * 演示电源操作演练段。powerAction 为空时跳过；填写 shutdown / reboot 时执行真实操作。
     * <p>
     * 关机是不可逆高危操作，重启会中断所有运行中的任务，请务必确认目标 NAS 后再启用。
     */
    private static void runPowerActionIfConfigured(SynologyDsmClient client, String powerAction) {
        if (!isConfigured(powerAction)) {
            log.info("powerAction 未配置，跳过电源操作段演示（shutdown / reboot 为高危操作，默认不执行）");
            return;
        }
        String action = powerAction.trim();
        if (POWER_ACTION_SHUTDOWN.equals(action)) {
            log.warn("即将对目标 NAS 执行关机操作！");
            client.coreSystem().info().shutdown();
            log.warn("关机指令已下发，NAS 正在关机");
        } else if (POWER_ACTION_REBOOT.equals(action)) {
            log.warn("即将对目标 NAS 执行重启操作！");
            client.coreSystem().info().reboot();
            log.warn("重启指令已下发，NAS 正在重启");
        } else {
            log.warn("powerAction 配置无法识别：{}（仅支持 shutdown / reboot），跳过电源操作段演示", action);
        }
    }

    /**
     * 启动时探测 SYNO.Core.System 与 SYNO.Core.System.Utilization 的真实版本范围，
     * 与 CoreSystemApi 中使用的版本对比，便于确认契约常量是否需要修正。
     */
    private static void probeApiVersions(SynologyDsmClient client) {
        ApiInfoResponse apiInfo = client.apiInfo().queryAll();
        printApiVersion(apiInfo, "SYNO.Core.System");
        printApiVersion(apiInfo, "SYNO.Core.System.Utilization");
    }

    private static void printApiVersion(ApiInfoResponse apiInfo, String apiName) {
        SynologyApiDescriptor descriptor = apiInfo.getApiDescriptor(apiName);
        if (descriptor == null) {
            log.warn("当前 DSM 不存在 API：{}", apiName);
            return;
        }
        log.info("API 版本探测：{}，minVersion={}，maxVersion={}",
                apiName, descriptor.getMinVersion(), descriptor.getMaxVersion());
    }

    private static int nz(Integer value) {
        return value == null ? 0 : value;
    }

    private static CoreSystemCoverageExampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = CoreSystemCoverageExample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, CoreSystemCoverageExampleConfig.class);
        }
    }

    private static String requiredConfigValue(String value, String fieldName) {
        if (!isConfigured(value)) {
            throw new IllegalArgumentException("示例配置文件 " + CONFIG_FILE + " 缺少必填配置：" + fieldName);
        }
        return value;
    }

    /**
     * 判断可选配置是否有效填写（非 null 且非空白）。
     */
    private static boolean isConfigured(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
