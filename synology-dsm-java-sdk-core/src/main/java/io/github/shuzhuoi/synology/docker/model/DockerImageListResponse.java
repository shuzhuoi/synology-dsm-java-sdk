package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Image list 方法的响应（本地镜像列表）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerImageListResponse {

    /**
     * 镜像列表。
     */
    private List<DockerImage> images;
    /**
     * 镜像总数。
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
