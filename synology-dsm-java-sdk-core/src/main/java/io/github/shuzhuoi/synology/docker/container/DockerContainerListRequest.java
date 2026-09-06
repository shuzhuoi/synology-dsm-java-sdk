package io.github.shuzhuoi.synology.docker.container;

import lombok.Getter;

/**
 * SYNO.Docker.Container list 方法的请求参数。
 */
@Getter
public class DockerContainerListRequest {

    /**
     * 起始序号，默认 0。
     */
    private final Integer offset;
    /**
     * 返回数量上限，-1 表示全部。
     */
    private final Integer limit;
    /**
     * 容器类型过滤，null 表示不过滤。
     */
    private final DockerContainerType type;

    private DockerContainerListRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.type = builder.type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer offset;
        private Integer limit;
        private DockerContainerType type;

        /**
         * 设置起始序号。
         */
        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        /**
         * 设置返回数量上限，-1 表示全部。
         */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /**
         * 设置容器类型过滤。
         */
        public Builder type(DockerContainerType type) {
            this.type = type;
            return this;
        }

        public DockerContainerListRequest build() {
            return new DockerContainerListRequest(this);
        }
    }
}
