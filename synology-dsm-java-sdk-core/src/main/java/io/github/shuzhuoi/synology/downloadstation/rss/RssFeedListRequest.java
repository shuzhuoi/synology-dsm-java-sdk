package io.github.shuzhuoi.synology.downloadstation.rss;

import lombok.Getter;

/**
 * SYNO.DownloadStation2.RSS.Feed list 方法的请求参数。
 */
@Getter
public class RssFeedListRequest {

    /**
     * 所属 RSS 站点 ID。
     */
    private final String siteId;
    /**
     * 起始序号。
     */
    private final Integer offset;
    /**
     * 返回数量上限，-1 表示全部。
     */
    private final Integer limit;

    private RssFeedListRequest(Builder builder) {
        this.siteId = builder.siteId;
        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    public static Builder builder(String siteId) {
        return new Builder(siteId);
    }

    public static class Builder {

        private final String siteId;
        private Integer offset;
        private Integer limit;

        private Builder(String siteId) {
            this.siteId = siteId;
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

        public RssFeedListRequest build() {
            return new RssFeedListRequest(this);
        }
    }
}
