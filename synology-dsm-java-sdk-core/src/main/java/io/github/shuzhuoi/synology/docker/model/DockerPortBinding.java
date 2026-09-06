package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 单条宿主机端口绑定。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerPortBinding {

    /**
     * 宿主机 IP，通常为空字符串（表示绑定全部网卡）。
     */
    @SynologyJsonProperty("HostIp")
    private String hostIp;
    /**
     * 宿主机端口。
     */
    @SynologyJsonProperty("HostPort")
    private String hostPort;
}
