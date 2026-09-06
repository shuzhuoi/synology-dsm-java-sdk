package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Docker inspect 的 NetworkSettings 结构（简化建模）。
 * <p>
 * 只保留各网络的连接信息；完整结构请通过 get（inspect 详情）响应核对。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerNetworkSettings {

    /**
     * 网络名称到连接信息的映射，例如 bridge、host。
     */
    @SynologyJsonProperty("Networks")
    private Map<String, DockerContainerNetwork> networks;
}
