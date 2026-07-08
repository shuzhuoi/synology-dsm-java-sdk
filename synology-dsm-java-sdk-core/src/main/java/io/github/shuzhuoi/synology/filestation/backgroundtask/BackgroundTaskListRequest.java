package io.github.shuzhuoi.synology.filestation.backgroundtask;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.BackgroundTask list 方法的请求参数。
 * <p>
 * 用于列出 copy、move、delete、compress、extract 等后台任务。
 */
@Getter
public class BackgroundTaskListRequest {

    /**
     * 跳过多少条后台任务。
     */
    private final Integer offset;
    /**
     * 返回多少条后台任务。0 表示全部。
     */
    private final Integer limit;
    /**
     * 排序字段：crtime（创建时间）或 finished（是否完成）。
     */
    private final String sortBy;
    /**
     * 排序方向：asc 或 desc。
     */
    private final String sortDirection;
    /**
     * 按 API 名称过滤，例如 SYNO.FileStation.CopyMove、SYNO.FileStation.Delete。
     */
    private final List<String> apiFilter;

    private BackgroundTaskListRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.apiFilter = Collections.unmodifiableList(new ArrayList<String>(builder.apiFilter));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer offset;
        private Integer limit;
        private String sortBy;
        private String sortDirection;
        private List<String> apiFilter = new ArrayList<String>();

        private Builder() {
        }

        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder sortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }

        /**
         * 追加一个 API 名称过滤条件，例如 SYNO.FileStation.CopyMove。
         */
        public Builder addApiFilter(String apiName) {
            if (apiName != null && apiName.trim().length() > 0) {
                this.apiFilter.add(apiName);
            }
            return this;
        }

        public BackgroundTaskListRequest build() {
            return new BackgroundTaskListRequest(this);
        }
    }
}
