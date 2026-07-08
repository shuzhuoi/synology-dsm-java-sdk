package io.github.shuzhuoi.synology.filestation.search;

import io.github.shuzhuoi.synology.model.Additional;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Search list 方法的请求参数。
 * <p>
 * list 用于从搜索临时数据库中分页获取匹配结果，同时返回 finished 标记。
 */
@Getter
public class SearchListRequest {

    /**
     * start 方法返回的搜索任务 ID。
     */
    private final String taskid;
    /**
     * 跳过多少条匹配结果。
     */
    private final Integer offset;
    /**
     * 返回多少条匹配结果。-1 表示全部，0 表示不返回。
     */
    private final Integer limit;
    /**
     * 排序字段：name、size、user、group、mtime、atime、ctime、crtime、posix、type。
     */
    private final String sortBy;
    /**
     * 排序方向：asc 或 desc。
     */
    private final String sortDirection;
    /**
     * 二次过滤的文件名 glob 模式。
     */
    private final String pattern;
    /**
     * 文件类型过滤：file、dir 或 all。
     */
    private final String filetype;
    /**
     * 额外返回的文件属性，例如 size、owner、time、perm。
     */
    private final List<Additional> additional;

    private SearchListRequest(Builder builder) {
        this.taskid = builder.taskid;
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.pattern = builder.pattern;
        this.filetype = builder.filetype;
        this.additional = Collections.unmodifiableList(new ArrayList<Additional>(builder.additional));
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
        private String pattern;
        private String filetype;
        private List<Additional> additional = new ArrayList<Additional>();

        private Builder(String taskid) {
            this.taskid = taskid;
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

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder filetype(String filetype) {
            this.filetype = filetype;
            return this;
        }

        public Builder addAdditional(Additional additional) {
            if (additional != null) {
                this.additional.add(additional);
            }
            return this;
        }

        public SearchListRequest build() {
            return new SearchListRequest(this);
        }
    }
}
