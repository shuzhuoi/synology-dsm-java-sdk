package io.github.shuzhuoi.synology.downloadstation.btsearch;

import lombok.Getter;

/**
 * SYNO.DownloadStation2.Task.BTSearch list 方法的请求参数。
 */
@Getter
public class BtSearchListRequest {

    /**
     * 搜索任务 ID（start 返回的 taskid）。
     */
    private final String taskid;
    /**
     * 起始序号。
     */
    private final Integer offset;
    /**
     * 返回数量上限。
     */
    private final Integer limit;
    /**
     * 排序字段：title、size、date、peers、seeds、leechs 等。
     */
    private final String sortBy;
    /**
     * 排序方向：asc 或 desc。
     */
    private final String sortDirection;
    /**
     * 按分类过滤。
     */
    private final String filterCategory;
    /**
     * 按标题过滤。
     */
    private final String filterTitle;

    private BtSearchListRequest(Builder builder) {
        this.taskid = builder.taskid;
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.filterCategory = builder.filterCategory;
        this.filterTitle = builder.filterTitle;
    }

    public static Builder builder(String taskid) {
        return new Builder(taskid);
    }

    public static class Builder {

        private final String taskid;
        private Integer offset;
        private Integer limit;
        private String sortBy;
        private String sortDirection;
        private String filterCategory;
        private String filterTitle;

        private Builder(String taskid) {
            this.taskid = taskid;
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

        /**
         * 设置排序字段（title/size/date/peers/seeds/leechs 等）。
         */
        public Builder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        /**
         * 设置排序方向（asc/desc）。
         */
        public Builder sortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }

        /**
         * 设置分类过滤。
         */
        public Builder filterCategory(String filterCategory) {
            this.filterCategory = filterCategory;
            return this;
        }

        /**
         * 设置标题过滤。
         */
        public Builder filterTitle(String filterTitle) {
            this.filterTitle = filterTitle;
            return this;
        }

        public BtSearchListRequest build() {
            return new BtSearchListRequest(this);
        }
    }
}
