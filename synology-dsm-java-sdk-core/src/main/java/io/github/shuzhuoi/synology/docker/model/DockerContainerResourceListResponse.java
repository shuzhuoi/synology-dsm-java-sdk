package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Container.Resource get 方法的响应（全部容器资源占用列表）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerResourceListResponse {

    /**
     * 各容器资源占用列表，已停止的容器 cpu / memory 为 0。
     */
    private List<DockerContainerResource> resources;
}
