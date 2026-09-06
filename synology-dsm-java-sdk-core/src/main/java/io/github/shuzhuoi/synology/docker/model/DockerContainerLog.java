package io.github.shuzhuoi.synology.docker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.Docker.Container.Log get 返回的单条容器日志。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerContainerLog {

    /**
     * 日志产生时间，ISO 8601 字符串。
     */
    private String created;
    /**
     * 日志文本内容。
     */
    private String text;
    /**
     * 日志流，stdout 或 stderr。
     */
    private String stream;
    /**
     * 日志文档 ID，用于导出等场景。
     */
    private String docid;
}
