package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Registry v2 tags 方法的响应（镜像标签列表）。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerRegistryTagListResponse {

    /**
     * 标签列表，例如 ["latest", "16", "16-alpine"]。
     */
    private List<String> tags;
}
