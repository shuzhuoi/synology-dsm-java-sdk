package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Network list 方法的响应（Docker 网络列表）。
 * <p>
 * 注意响应字段名是 network（单数），值为网络数组。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerNetworkListResponse {

    /**
     * 网络列表。
     */
    private List<DockerNetwork> network;
}
