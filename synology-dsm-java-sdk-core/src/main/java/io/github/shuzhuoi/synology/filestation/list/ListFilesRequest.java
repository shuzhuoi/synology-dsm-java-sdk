package io.github.shuzhuoi.synology.filestation.list;

import io.github.shuzhuoi.synology.model.Additional;
import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ListFilesRequest {

    /**
     * 要枚举的目录路径，必须从共享目录开始，例如 /photo 或 /home/demo。
     */
    private final String folderPath;
    /**
     * 跳过多少条记录。
     */
    private final Integer offset;
    /**
     * 返回多少条记录，0 表示由 DSM 按默认规则返回全部。
     */
    private final Integer limit;
    /**
     * 排序字段，例如 name、size、mtime。
     */
    private final String sortBy;
    /**
     * 排序方向，asc 或 desc。
     */
    private final String sortDirection;
    /**
     * 文件名匹配模式，支持 DSM 文档中的 glob 规则。
     */
    private final String pattern;
    /**
     * 文件类型过滤：file、dir 或 all。
     */
    private final String filetype;
    /**
     * 跳转路径，需要配合 additional=real_path 使用。
     */
    private final String gotoPath;
    /**
     * 需要额外返回的文件属性，例如 size、owner、time、perm。
     */
    private final List<Additional> additional;

    private ListFilesRequest(Builder builder) {
        this.folderPath = SynologyPath.normalize(builder.folderPath);
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.pattern = builder.pattern;
        this.filetype = builder.filetype;
        this.gotoPath = builder.gotoPath;
        this.additional = Collections.unmodifiableList(new ArrayList<Additional>(builder.additional));
    }

    public static Builder builder(String folderPath) {
        return new Builder(folderPath);
    }

    public static class Builder {
        private final String folderPath;
        private Integer offset;
        private Integer limit;
        private String sortBy;
        private String sortDirection;
        private String pattern;
        private String filetype;
        private String gotoPath;
        private List<Additional> additional = new ArrayList<Additional>();

        private Builder(String folderPath) {
            this.folderPath = folderPath;
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

        public Builder gotoPath(String gotoPath) {
            this.gotoPath = gotoPath;
            return this;
        }

        public Builder addAdditional(Additional additional) {
            if (additional != null) {
                this.additional.add(additional);
            }
            return this;
        }

        public ListFilesRequest build() {
            return new ListFilesRequest(this);
        }
    }
}
