package io.github.shuzhuoi.synology.docker.log;

import lombok.Getter;

/**
 * SYNO.Docker.Container.Log get 方法的请求参数。
 * <p>
 * 过滤参数与 DSM Container Manager UI 的日志过滤载荷一致。
 */
@Getter
public class DockerContainerLogRequest {

    /**
     * 容器名称（必填）。
     */
    private final String name;
    /**
     * 起始时间过滤，格式 yyyy-MM-ddTHH:mm:ss，null 表示不过滤。
     */
    private final String from;
    /**
     * 结束时间过滤，格式 yyyy-MM-ddTHH:mm:ss，null 表示不过滤。
     */
    private final String to;
    /**
     * 日志文本关键字过滤，null 表示不过滤。
     */
    private final String keyword;
    /**
     * 日志级别过滤，null 或空表示全部级别。
     */
    private final String level;
    /**
     * 排序方向：ASC / DESC，null 表示由 DSM 决定。
     */
    private final String sortDir;
    /**
     * 起始序号，默认 0。
     */
    private final Integer offset;
    /**
     * 返回数量上限。
     */
    private final Integer limit;

    private DockerContainerLogRequest(Builder builder) {
        this.name = builder.name;
        this.from = builder.from;
        this.to = builder.to;
        this.keyword = builder.keyword;
        this.level = builder.level;
        this.sortDir = builder.sortDir;
        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private final String name;
        private String from;
        private String to;
        private String keyword;
        private String level;
        private String sortDir;
        private Integer offset;
        private Integer limit;

        private Builder(String name) {
            this.name = name;
        }

        /**
         * 设置起始时间过滤。
         */
        public Builder from(String from) {
            this.from = from;
            return this;
        }

        /**
         * 设置结束时间过滤。
         */
        public Builder to(String to) {
            this.to = to;
            return this;
        }

        /**
         * 设置日志文本关键字过滤。
         */
        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        /**
         * 设置日志级别过滤。
         */
        public Builder level(String level) {
            this.level = level;
            return this;
        }

        /**
         * 设置排序方向，ASC / DESC。
         */
        public Builder sortDir(String sortDir) {
            this.sortDir = sortDir;
            return this;
        }

        /**
         * 设置起始序号。
         */
        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        /**
         * 设置返回数量上限。
         */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public DockerContainerLogRequest build() {
            return new DockerContainerLogRequest(this);
        }
    }
}
