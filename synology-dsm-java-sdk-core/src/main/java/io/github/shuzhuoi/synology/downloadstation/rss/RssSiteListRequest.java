package io.github.shuzhuoi.synology.downloadstation.rss;

import lombok.Getter;

/**
 * SYNO.DownloadStation2.RSS.Site list 方法的请求参数。
 */
@Getter
public class RssSiteListRequest {

    /**
     * 起始序号。
     */
    private final Integer offset;
    /**
     * 返回数量上限，-1 表示全部。
     */
    private final Integer limit;

    private RssSiteListRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer offset;
        private Integer limit;

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

        public RssSiteListRequest build() {
            return new RssSiteListRequest(this);
        }
    }
}
