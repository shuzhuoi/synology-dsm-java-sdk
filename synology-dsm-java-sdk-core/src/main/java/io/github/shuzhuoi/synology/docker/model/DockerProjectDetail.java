package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Project get 返回的单个项目详情。
 * <p>
 * 在 {@link DockerProject}（list 返回的项目摘要字段）基础上，
 * 额外携带项目内全部容器的 inspect 结构与 compose 文件内容。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerProjectDetail extends DockerProject {

    /**
     * 项目内全部容器的 Docker inspect 详情（含配置、状态、网络等）。
     */
    private List<DockerContainerDetail> containers;
    /**
     * 项目的 docker-compose.yml 文件内容（YAML 文本）。
     */
    private String content;
}
