package io.github.shuzhuoi.synology.docker.resource;

import io.github.shuzhuoi.synology.docker.DockerApi;
import io.github.shuzhuoi.synology.docker.model.DockerContainerResourceListResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

import java.util.Collections;

/**
 * SYNO.Docker.Container.Resource 客户端。
 * <p>
 * 提供全部容器的实时资源占用查询（CPU / 内存），等价于 DSM 容器总览页的统计信息。
 */
public class DockerContainerResourceClient {

    private final SynologyApiExecutor executor;

    public DockerContainerResourceClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    /**
     * 查询全部容器的实时资源占用。
     * <p>
     * 该接口不支持按容器过滤，如需单个容器数据请在结果列表中按 name 查找。
     *
     * @return 各容器资源占用列表
     */
    public DockerContainerResourceListResponse get() {
        // get 无业务参数，返回全部容器资源占用。
        return executor.getAuthenticated(
                "entry.cgi",
                DockerApi.CONTAINER_RESOURCE_API,
                DockerApi.CONTAINER_RESOURCE_VERSION,
                "get",
                Collections.<String, String>emptyMap(),
                DockerContainerResourceListResponse.class
        );
    }
}
