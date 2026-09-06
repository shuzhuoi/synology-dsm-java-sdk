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
import io.github.shuzhuoi.synology.docker.model.DockerImage;
import io.github.shuzhuoi.synology.docker.model.DockerImageDetail;
import io.github.shuzhuoi.synology.docker.model.DockerImageListResponse;
import io.github.shuzhuoi.synology.docker.model.DockerImagePullStartResponse;
import io.github.shuzhuoi.synology.docker.model.DockerImagePullStatusResponse;
import io.github.shuzhuoi.synology.docker.model.DockerPortBinding;
import io.github.shuzhuoi.synology.docker.model.DockerProject;
import io.github.shuzhuoi.synology.docker.model.DockerProjectDetail;
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
 * 演示 SYNO.Docker.Container / SYNO.Docker.Container.Resource / SYNO.Docker.Container.Log /
 * SYNO.Docker.Image / SYNO.Docker.Project 的能力：
 * 容器列表（含运行状态过滤）、容器详情（inspect，含端口映射）、实时资源占用（CPU / 内存）、
 * 容器日志查询、镜像列表与详情、镜像拉取（pull_start + pull_status 两步异步流程）、
 * Compose 项目列表与详情，以及可选的生命周期演练（默认跳过）。
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
    /**
     * 拉取演练的轮询间隔（毫秒）与最大轮询次数。
     */
    private static final long PULL_POLL_INTERVAL_MILLIS = 2000L;
    private static final int PULL_POLL_MAX_TIMES = 60;
    private static final String PROJECT_ACTION_START = "start";
    private static final String PROJECT_ACTION_STOP = "stop";
    private static final String PROJECT_ACTION_RESTART = "restart";
    private static final String PROJECT_ACTION_CLEAN = "clean";
    private static final String PROJECT_ACTION_DELETE = "delete";

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
            showImageList(client);
            showImageDetailIfConfigured(client, sampleConfig.getImageName());
            pullImageIfConfigured(client, sampleConfig.getPullRepository(), sampleConfig.getPullTag());
            showProjectList(client);
            showProjectDetailIfConfigured(client, sampleConfig.getProjectName());
            runProjectActionIfConfigured(client, sampleConfig.getProjectName(), sampleConfig.getProjectAction());
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
     * 演示镜像列表查询：全部本地镜像（不含 DSM 系统镜像）。
     */
    private static void showImageList(SynologyDsmClient client) {
        DockerImageListResponse response = client.docker().image().list();
        List<DockerImage> images = response.getImages();
        log.info("镜像列表：总数={}", response.getTotal());
        if (images != null) {
            for (DockerImage image : images) {
                log.info("镜像：{}:{}，大小={}MB，创建时间={}，可更新={}",
                        image.getRepository(), image.getTags(),
                        image.getSize() == null ? null : image.getSize() / 1024 / 1024,
                        image.getCreated(), image.getUpgradable());
            }
        }
    }

    /**
     * 演示镜像详情查询。imageName 为空时跳过，tag 固定使用 latest。
     */
    private static void showImageDetailIfConfigured(SynologyDsmClient client, String imageName) {
        if (!isConfigured(imageName)) {
            log.info("imageName 未配置，跳过镜像详情段演示");
            return;
        }
        String repository = imageName.trim();
        DockerImageDetail detail = client.docker().image().get(repository);
        log.info("镜像详情：仓库={}，标签={}，大小={}MB，虚拟大小={}MB",
                detail.getRepository(), detail.getTags(),
                detail.getSize() == null ? null : detail.getSize() / 1024 / 1024,
                detail.getVirtualSize() == null ? null : detail.getVirtualSize() / 1024 / 1024);
    }

    /**
     * 演示镜像拉取的两步异步流程：pull_start 发起任务，pull_status 轮询直到结束。
     * pullRepository 为空时跳过；拉取会占用网络带宽与磁盘空间。
     */
    private static void pullImageIfConfigured(SynologyDsmClient client, String pullRepository, String pullTag) {
        if (!isConfigured(pullRepository)) {
            log.info("pullRepository 未配置，跳过镜像拉取段演示（拉取会占用带宽与磁盘，默认不执行）");
            return;
        }
        String repository = pullRepository.trim();
        String tag = isConfigured(pullTag) ? pullTag.trim() : "latest";
        log.warn("即将拉取镜像：{}:{}（会占用网络带宽与磁盘空间）", repository, tag);

        DockerImagePullStartResponse start = client.docker().image().pullStart(repository, tag);
        String taskId = start.getTaskId();
        log.info("拉取任务已发起：task_id={}", taskId);

        for (int i = 0; i < PULL_POLL_MAX_TIMES; i++) {
            sleepQuietly(PULL_POLL_INTERVAL_MILLIS);
            DockerImagePullStatusResponse status = client.docker().image().pullStatus(taskId);
            log.info("拉取进度：status={}，progress={}", status.getStatus(), status.getProgress());
            if (status.getStatus() == null || "processing".equals(status.getStatus())) {
                continue;
            }
            if ("error".equals(status.getStatus())) {
                log.warn("拉取失败：{}", status.getError());
            } else {
                log.info("拉取完成：status={}", status.getStatus());
            }
            return;
        }
        log.warn("拉取任务轮询超过 {} 次仍未结束，请到 Container Manager UI 查看最终状态", PULL_POLL_MAX_TIMES);
    }

    /**
     * 演示 Compose 项目列表查询：data 是以项目 UUID 为键的 map。
     */
    private static void showProjectList(SynologyDsmClient client) {
        Map<String, DockerProject> projects = client.docker().project().list();
        log.info("项目列表：共 {} 项", projects == null ? 0 : projects.size());
        if (projects != null) {
            for (Map.Entry<String, DockerProject> entry : projects.entrySet()) {
                DockerProject project = entry.getValue();
                log.info("项目：{}（id={}），状态={}，路径={}，容器数={}",
                        project.getName(), entry.getKey(), project.getStatus(),
                        project.getPath(),
                        project.getContainerIds() == null ? 0 : project.getContainerIds().size());
            }
        }
    }

    /**
     * 演示项目详情查询（含容器 inspect 与 compose 文件内容）。projectName 为空时跳过。
     */
    private static void showProjectDetailIfConfigured(SynologyDsmClient client, String projectName) {
        if (!isConfigured(projectName)) {
            log.info("projectName 未配置，跳过项目详情段演示");
            return;
        }
        String name = projectName.trim();
        DockerProject summary = client.docker().project().findByName(name);
        if (summary == null) {
            log.warn("未找到名称为 {} 的项目，跳过项目详情段演示", name);
            return;
        }
        DockerProjectDetail detail = client.docker().project().get(summary.getId());
        log.info("项目详情：名称={}，状态={}，容器数={}，compose 文件长度={} 字符",
                detail.getName(), detail.getStatus(),
                detail.getContainers() == null ? 0 : detail.getContainers().size(),
                detail.getContent() == null ? 0 : detail.getContent().length());
    }

    /**
     * 演示项目生命周期操作演练段。projectAction 为空时跳过；
     * 填写 start / stop / restart / clean / delete 时执行真实操作。
     * <p>
     * clean 会停止并移除项目创建的容器（docker compose down 语义），
     * delete 会删除整个项目，均为高危操作，请务必确认目标项目后再启用。
     */
    private static void runProjectActionIfConfigured(SynologyDsmClient client, String projectName, String projectAction) {
        if (!isConfigured(projectAction)) {
            log.info("projectAction 未配置，跳过项目生命周期操作段演示（clean / delete 为高危操作，默认不执行）");
            return;
        }
        if (!isConfigured(projectName)) {
            log.warn("projectAction 已配置但 projectName 为空，跳过项目生命周期操作段演示");
            return;
        }
        String name = projectName.trim();
        String action = projectAction.trim();
        DockerProject summary = client.docker().project().findByName(name);
        if (summary == null) {
            log.warn("未找到名称为 {} 的项目，跳过项目生命周期操作段演示", name);
            return;
        }
        String projectId = summary.getId();
        if (PROJECT_ACTION_START.equals(action)) {
            log.warn("即将启动项目：{}（id={}）", name, projectId);
            client.docker().project().start(projectId);
            log.warn("启动指令已下发");
        } else if (PROJECT_ACTION_STOP.equals(action)) {
            log.warn("即将停止项目：{}（id={}），项目内容器会停止服务", name, projectId);
            client.docker().project().stop(projectId);
            log.warn("停止指令已下发");
        } else if (PROJECT_ACTION_RESTART.equals(action)) {
            log.warn("即将重启项目：{}（id={}）", name, projectId);
            client.docker().project().restart(projectId);
            log.warn("重启指令已下发");
        } else if (PROJECT_ACTION_CLEAN.equals(action)) {
            log.warn("即将清理项目：{}（id={}），项目创建的容器与网络会被移除（compose down 语义）", name, projectId);
            client.docker().project().clean(projectId);
            log.warn("清理指令已下发");
        } else if (PROJECT_ACTION_DELETE.equals(action)) {
            log.warn("即将删除项目：{}（id={}），项目会被整体删除且不可恢复！", name, projectId);
            client.docker().project().delete(projectId);
            log.warn("删除指令已下发");
        } else {
            log.warn("projectAction 配置无法识别：{}（仅支持 start / stop / restart / clean / delete），跳过项目生命周期操作段演示", action);
        }
    }

    /**
     * 线程内安静休眠，仅供拉取轮询等待使用。
     */
    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 启动时探测 Docker API 的真实版本范围，
     * 与 DockerApi 中使用的版本对比，便于确认契约常量是否需要修正。
     */
    private static void probeApiVersions(SynologyDsmClient client) {
        ApiInfoResponse apiInfo = client.apiInfo().queryAll();
        printApiVersion(apiInfo, "SYNO.Docker.Container");
        printApiVersion(apiInfo, "SYNO.Docker.Container.Resource");
        printApiVersion(apiInfo, "SYNO.Docker.Container.Log");
        printApiVersion(apiInfo, "SYNO.Docker.Image");
        printApiVersion(apiInfo, "SYNO.Docker.Project");
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
