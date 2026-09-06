package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Container.Log get 方法的响应（容器日志列表）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerLogListResponse {

    /**
     * 日志条目列表。
     */
    private List<DockerContainerLog> logs;
    /**
     * 符合条件的日志总数。
     */
    private Integer total;
    /**
     * 当前偏移量。
     */
    private Integer offset;
    /**
     * 当前查询上限。
     */
    private Integer limit;
}
