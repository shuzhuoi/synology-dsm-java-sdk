package io.github.shuzhuoi.synology.docker.container;

/**
 * SYNO.Docker.Container list 方法的容器类型过滤。
 */
public enum DockerContainerType {

    /**
     * 全部容器。
     */
    ALL("all"),
    /**
     * 仅运行中的容器。
     */
    RUNNING("running"),
    /**
     * 已停止的容器。
     */
    STOPPED("stopped");

    private final String value;

    DockerContainerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
