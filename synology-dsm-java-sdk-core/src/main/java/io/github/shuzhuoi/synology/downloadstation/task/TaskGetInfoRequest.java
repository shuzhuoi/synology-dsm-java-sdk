package io.github.shuzhuoi.synology.downloadstation.task;

import io.github.shuzhuoi.synology.downloadstation.model.TaskAdditionalField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.DownloadStation2.Task getinfo 方法的请求参数。
 */
@Getter
public class TaskGetInfoRequest {

    /**
     * 要查询的任务 ID 列表。
     */
    private final List<String> ids;
    /**
     * 请求的扩展信息块。
     */
    private final List<TaskAdditionalField> additionals;

    private TaskGetInfoRequest(Builder builder) {
        this.ids = Collections.unmodifiableList(new ArrayList<String>(builder.ids));
        this.additionals = Collections.unmodifiableList(new ArrayList<TaskAdditionalField>(builder.additionals));
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {

        private final List<String> ids = new ArrayList<String>();
        private final List<TaskAdditionalField> additionals = new ArrayList<TaskAdditionalField>();

        private Builder(String id) {
            this.ids.add(id);
        }

        /**
         * 追加一个要查询的任务 ID。
         */
        public Builder addId(String id) {
            this.ids.add(id);
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

        public TaskGetInfoRequest build() {
            return new TaskGetInfoRequest(this);
        }
    }
}
