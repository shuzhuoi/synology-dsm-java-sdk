package io.github.shuzhuoi.synology.downloadstation.task;

import io.github.shuzhuoi.synology.downloadstation.model.TaskAdditionalField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.DownloadStation2.Task list 方法的请求参数。
 */
@Getter
public class TaskListRequest {

    /**
     * 起始序号，默认 0。
     */
    private final Integer offset;
    /**
     * 返回数量上限，-1 表示全部。
     */
    private final Integer limit;
    /**
     * 请求的扩展信息块（detail/transfer/file/tracker/peer）。
     */
    private final List<TaskAdditionalField> additionals;

    private TaskListRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.additionals = Collections.unmodifiableList(new ArrayList<TaskAdditionalField>(builder.additionals));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer offset;
        private Integer limit;
        private final List<TaskAdditionalField> additionals = new ArrayList<TaskAdditionalField>();

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
         * 追加一个扩展信息块请求。
         */
        public Builder addAdditional(TaskAdditionalField additional) {
            if (additional != null) {
                this.additionals.add(additional);
            }
            return this;
        }

        public TaskListRequest build() {
            return new TaskListRequest(this);
        }
    }
}
