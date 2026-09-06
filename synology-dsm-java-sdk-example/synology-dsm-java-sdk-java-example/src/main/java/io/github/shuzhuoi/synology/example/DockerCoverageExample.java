package io.github.shuzhuoi.synology.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.api.ApiInfoResponse;
import io.github.shuzhuoi.synology.api.SynologyApiDescriptor;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.docker.container.DockerContainerListRequest;
import io.github.shuzhuoi.synology.docker.container.DockerContainerType;
import io.github.shuzhuoi.synology.docker.log.DockerContainerLogRequest;
import io.github.shuzhuoi.synology.docker.model.DockerContainer;
import io.github.shuzhuoi.synology.docker.model.DockerContainerDetail;
import io.github.shuzhuoi.synology.docker.model.DockerContainerListResponse;
import io.github.shuzhuoi.synology.docker.model.DockerContainerLog;
import io.github.shuzhuoi.synology.docker.model.DockerContainerLogListResponse;
import io.github.shuzhuoi.synology.docker.model.DockerContainerResource;
import io.github.shuzhuoi.synology.docker.model.DockerContainerResourceListResponse;
import io.github.shuzhuoi.synology.docker.model.DockerPortBinding;
import io.github.shuzhuoi.synology.example.config.DockerCoverageExampleConfig;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Docker 官方契约覆盖示例。
 * <p>
 * 演示 SYNO.Docker.Container / SYNO.Docker.Container.Resource / SYNO.Docker.Container.Log
 * 的第一批能力：容器列表（含运行状态过滤）、容器详情（inspect，含端口映射）、
 * 实时资源占用（CPU / 内存）、容器日志查询，以及可选的生命周期演练
 * （start / stop / restart，默认跳过）。
 * <p>
 * 运行前请复制 classpath 下的 docker-coverage.example.yaml 为
 * docker-coverage.yaml，并填写真实 DSM 地址、账号和密码。
 * 注意：SYNO.Docker.* 通常需要管理员或具有 Container Manager 权限的账户，
 * 普通账户调用会返回 105（insufficient privilege）错误。
 */
@Slf4j
public class DockerCoverageExample {

    private static final String CONFIG_FILE = "docker-coverage.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "docker-coverage.example.yaml";
    private static final String LIFECYCLE_ACTION_START = "start";
    private static final String LIFECYCLE_ACTION_STOP = "stop";
    private static final String LIFECYCLE_ACTION_RESTART = "restart";

    public static void main(String[] args) throws IOException {
        DockerCoverageExampleConfig sampleConfig = readSampleConfig();
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
            showContainerList(client);
            showResources(client);
            showContainerDetailAndLogsIfConfigured(client, sampleConfig.getContainerName());
            runLifecycleActionIfConfigured(client, sampleConfig.getContainerName(), sampleConfig.getLifecycleAction());
            log.info("Docker 官方覆盖示例执行完成");
        } finally {
            client.session().logout();
        }
    }

    /**
     * 演示容器列表查询：全部容器与运行中容器两种过滤。
     */
    private static void showContainerList(SynologyDsmClient client) {
        DockerContainerListResponse all = client.docker().container().list(DockerContainerListRequest.builder()
                .offset(0)
                .limit(-1)
                .build());
        log.info("容器列表：总数={}（全部）", all.getTotal());
        printContainers(all.getContainers());

        DockerContainerListResponse running = client.docker().container().list(DockerContainerListRequest.builder()
                .offset(0)
                .limit(-1)
                .type(DockerContainerType.RUNNING)
                .build());
        log.info("容器列表：总数={}（仅运行中）", running.getTotal());
        printContainers(running.getContainers());
    }

    private static void printContainers(List<DockerContainer> containers) {
        if (containers == null) {
            return;
        }
        for (DockerContainer container : containers) {
            String running = container.getState() != null && Boolean.TRUE.equals(container.getState().getRunning())
                    ? "运行中" : "已停止";
            String ipAddress = container.getNetworkSettings() != null
                    && container.getNetworkSettings().getNetworks() != null
                    && !container.getNetworkSettings().getNetworks().isEmpty()
                    ? container.getNetworkSettings().getNetworks().values().iterator().next().getIpAddress()
                    : null;
            log.info("容器：{}（{}）镜像={}，状态={}，{}，IP={}",
                    container.getName(), running, container.getImage(),
                    container.getStatus(), container.getUpStatus(), ipAddress);
        }
    }

    /**
     * 演示实时资源占用查询：各容器的 CPU 与内存占用。
     */
    private static void showResources(SynologyDsmClient client) {
        DockerContainerResourceListResponse response = client.docker().resource().get();
        List<DockerContainerResource> resources = response.getResources();
        log.info("容器资源占用：共 {} 项", resources == null ? 0 : resources.size());
        if (resources != null) {
            for (DockerContainerResource resource : resources) {
                log.info("容器：{} CPU={}%，内存={}MB（{}%）",
                        resource.getName(), resource.getCpu(),
                        resource.getMemory() == null ? null : resource.getMemory() / 1024 / 1024,
                        resource.getMemoryPercent());
            }
        }
    }

    /**
     * 演示容器详情（inspect）与日志查询。containerName 为空时跳过。
     */
    private static void showContainerDetailAndLogsIfConfigured(SynologyDsmClient client, String containerName) {
        if (!isConfigured(containerName)) {
            log.info("containerName 未配置，跳过详情与日志段演示");
            return;
        }
        String name = containerName.trim();

        DockerContainerDetail detail = client.docker().container().get(name);
        log.info("容器详情：名称={}，镜像={}，主机名={}",
                detail.getName(), detail.getConfig() == null ? null : detail.getConfig().getImage(),
                detail.getConfig() == null ? null : detail.getConfig().getHostname());
        if (detail.getHostConfig() != null && detail.getHostConfig().getPortBindings() != null) {
            for (Map.Entry<String, List<DockerPortBinding>> entry
                    : detail.getHostConfig().getPortBindings().entrySet()) {
                for (DockerPortBinding binding : entry.getValue()) {
                    log.info("端口映射：容器 {} -> 宿主机 {}:{}",
                            entry.getKey(), binding.getHostIp(), binding.getHostPort());
                }
            }
        }

        DockerContainerLogListResponse logs = client.docker().log().get(DockerContainerLogRequest.builder(name)
                .sortDir("DESC")
                .offset(0)
                .limit(20)
                .build());
        log.info("容器日志：总数={}（最近 20 条，倒序）", logs.getTotal());
        if (logs.getLogs() != null) {
            for (DockerContainerLog logEntry : logs.getLogs()) {
                log.info("日志：[{}] {}", logEntry.getStream(), logEntry.getText());
            }
        }
    }

    /**
     * 演示生命周期操作演练段。lifecycleAction 为空时跳过；填写 start / stop / restart 时执行真实操作。
     * <p>
     * 停止容器会中断该容器的所有服务，请务必确认目标容器后再启用。
     */
    private static void runLifecycleActionIfConfigured(SynologyDsmClient client, String containerName, String lifecycleAction) {
        if (!isConfigured(lifecycleAction)) {
            log.info("lifecycleAction 未配置，跳过生命周期操作段演示（start / stop / restart 为高危操作，默认不执行）");
            return;
        }
        if (!isConfigured(containerName)) {
            log.warn("lifecycleAction 已配置但 containerName 为空，跳过生命周期操作段演示");
            return;
        }
        String name = containerName.trim();
        String action = lifecycleAction.trim();
        if (LIFECYCLE_ACTION_START.equals(action)) {
            log.warn("即将启动容器：{}", name);
            client.docker().container().start(name);
            log.warn("启动指令已下发");
        } else if (LIFECYCLE_ACTION_STOP.equals(action)) {
            log.warn("即将停止容器：{}（该容器的服务会中断）", name);
            client.docker().container().stop(name);
            log.warn("停止指令已下发");
        } else if (LIFECYCLE_ACTION_RESTART.equals(action)) {
            log.warn("即将重启容器：{}", name);
            client.docker().container().restart(name);
            log.warn("重启指令已下发");
        } else {
            log.warn("lifecycleAction 配置无法识别：{}（仅支持 start / stop / restart），跳过生命周期操作段演示", action);
        }
    }

    /**
     * 启动时探测三个 Docker API 的真实版本范围，
     * 与 DockerApi 中使用的版本对比，便于确认契约常量是否需要修正。
     */
    private static void probeApiVersions(SynologyDsmClient client) {
        ApiInfoResponse apiInfo = client.apiInfo().queryAll();
        printApiVersion(apiInfo, "SYNO.Docker.Container");
        printApiVersion(apiInfo, "SYNO.Docker.Container.Resource");
        printApiVersion(apiInfo, "SYNO.Docker.Container.Log");
    }

    private static void printApiVersion(ApiInfoResponse apiInfo, String apiName) {
        SynologyApiDescriptor descriptor = apiInfo.getApiDescriptor(apiName);
        if (descriptor == null) {
            log.warn("当前 DSM 不存在 API：{}（请确认已安装 Container Manager 套件）", apiName);
            return;
        }
        log.info("API 版本探测：{}，minVersion={}，maxVersion={}",
                apiName, descriptor.getMinVersion(), descriptor.getMaxVersion());
    }

    private static DockerCoverageExampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = DockerCoverageExample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, DockerCoverageExampleConfig.class);
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
