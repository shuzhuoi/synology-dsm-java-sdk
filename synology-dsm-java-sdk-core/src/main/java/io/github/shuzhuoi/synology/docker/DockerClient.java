package io.github.shuzhuoi.synology.docker;

import io.github.shuzhuoi.synology.docker.container.DockerContainerClient;
import io.github.shuzhuoi.synology.docker.image.DockerImageClient;
import io.github.shuzhuoi.synology.docker.log.DockerContainerLogClient;
import io.github.shuzhuoi.synology.docker.network.DockerNetworkClient;
import io.github.shuzhuoi.synology.docker.project.DockerProjectClient;
import io.github.shuzhuoi.synology.docker.registry.DockerRegistryClient;
import io.github.shuzhuoi.synology.docker.resource.DockerContainerResourceClient;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

/**
 * DSM Docker / Container Manager API 聚合入口。
 * <p>
 * 该类不直接发请求，只负责把容器管理能力按资源类型拆分到不同客户端中。
 */
public class DockerClient {

    /**
     * 容器列表、详情与生命周期接口。
     */
    private final DockerContainerClient containerClient;
    /**
     * 容器实时资源占用接口。
     */
    private final DockerContainerResourceClient resourceClient;
    /**
     * 容器日志查询接口。
     */
    private final DockerContainerLogClient logClient;
    /**
     * 镜像列表、详情、删除、清理与拉取接口。
     */
    private final DockerImageClient imageClient;
    /**
     * Compose 项目查询与生命周期接口。
     */
    private final DockerProjectClient projectClient;
    /**
     * Docker 网络查询、创建与删除接口。
     */
    private final DockerNetworkClient networkClient;
    /**
     * 注册表配置、镜像搜索与标签查询接口。
     */
    private final DockerRegistryClient registryClient;

    public DockerClient(SynologyApiExecutor executor) {
        this.containerClient = new DockerContainerClient(executor);
        this.resourceClient = new DockerContainerResourceClient(executor);
        this.logClient = new DockerContainerLogClient(executor);
        this.imageClient = new DockerImageClient(executor);
        this.projectClient = new DockerProjectClient(executor);
        this.networkClient = new DockerNetworkClient(executor);
        this.registryClient = new DockerRegistryClient(executor);
    }

    public DockerContainerClient container() {
        return containerClient;
    }

    public DockerContainerResourceClient resource() {
        return resourceClient;
    }

    public DockerContainerLogClient log() {
        return logClient;
    }

    public DockerImageClient image() {
        return imageClient;
    }

    public DockerProjectClient project() {
        return projectClient;
    }

    public DockerNetworkClient network() {
        return networkClient;
    }

    public DockerRegistryClient registry() {
        return registryClient;
    }
}
