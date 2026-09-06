package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.Docker.Container.Resource get 返回的单个容器资源占用。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerResource {

    /**
     * 容器名称。
     */
    private String name;
    /**
     * CPU 占用百分比。
     */
    private Double cpu;
    /**
     * 内存占用，单位字节。
     */
    private Long memory;
    /**
     * 内存占用百分比。
     */
    @SynologyJsonProperty("memoryPercent")
    private Double memoryPercent;
}
