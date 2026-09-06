package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Container list 方法的响应（容器列表）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerListResponse {

    /**
     * 容器列表。
     */
    private List<DockerContainer> containers;
    /**
     * 容器总数。
     */
    private Integer total;
    /**
     * 当前偏移量。
     */
    private Integer offset;
    /**
     * 当前查询上限，-1 表示全部。
     */
    private Integer limit;
}
