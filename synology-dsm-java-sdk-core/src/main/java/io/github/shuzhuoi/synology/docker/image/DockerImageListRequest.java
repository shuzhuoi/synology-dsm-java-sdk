package io.github.shuzhuoi.synology.docker.image;

import lombok.Getter;

/**
 * SYNO.Docker.Image list 方法的请求参数。
 */
@Getter
public class DockerImageListRequest {

    /**
     * 起始序号，默认 0。
     */
    private final Integer offset;
    /**
     * 返回数量上限，-1 表示全部。
     */
    private final Integer limit;
    /**
     * 是否包含 DSM 系统自带镜像，默认 false。
     */
    private final Boolean showDsm;

    private DockerImageListRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.showDsm = builder.showDsm;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer offset;
        private Integer limit;
        private Boolean showDsm;

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
         * 设置是否包含 DSM 系统自带镜像。
         */
        public Builder showDsm(Boolean showDsm) {
            this.showDsm = showDsm;
            return this;
        }

        public DockerImageListRequest build() {
            return new DockerImageListRequest(this);
        }
    }
}
