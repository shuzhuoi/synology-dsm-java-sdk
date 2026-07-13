package io.github.shuzhuoi.synology.filestation.search;

import io.github.shuzhuoi.synology.filestation.option.FileTypeFilter;
import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Search start 方法的请求参数。
 * <p>
 * 多个条件之间为 AND 关系。folder_path 支持多个目录同时搜索。
 */
@Getter
public class SearchStartRequest {

    /**
     * 搜索范围目录路径列表，必须从共享目录开始，例如 /video 或 /home/demo。
     */
    private final List<String> folderPaths;
    /**
     * 是否递归搜索子目录。官方默认 true。
     */
    private final Boolean recursive;
    /**
     * 文件名匹配模式，支持 glob 规则。不含 glob 语法时官方会自动前后补 *。
     */
    private final String pattern;
    /**
     * 文件扩展名匹配模式，逗号分隔多个 glob。给出该条件时文件夹不会被匹配。
     */
    private final String extension;
    /**
     * 文件类型过滤：file、dir 或 all。
     */
    private final String filetype;
    /**
     * 文件大小下限，单位字节。
     */
    private final Long sizeFrom;
    /**
     * 文件大小上限，单位字节。
     */
    private final Long sizeTo;
    /**
     * 最后修改时间下限，Linux 秒级时间戳。
     */
    private final Long mtimeFrom;
    /**
     * 最后修改时间上限，Linux 秒级时间戳。
     */
    private final Long mtimeTo;
    /**
     * 创建时间下限，Linux 秒级时间戳。
     */
    private final Long crtimeFrom;
    /**
     * 创建时间上限，Linux 秒级时间戳。
     */
    private final Long crtimeTo;
    /**
     * 最后访问时间下限，Linux 秒级时间戳。
     */
    private final Long atimeFrom;
    /**
     * 最后访问时间上限，Linux 秒级时间戳。
     */
    private final Long atimeTo;
    /**
     * 文件所属用户名，大小写不敏感。
     */
    private final String owner;
    /**
     * 文件所属组名，大小写不敏感。
     */
    private final String group;

    private SearchStartRequest(Builder builder) {
        this.folderPaths = Collections.unmodifiableList(new ArrayList<String>(builder.folderPaths));
        this.recursive = builder.recursive;
        this.pattern = builder.pattern;
        this.extension = builder.extension;
        this.filetype = builder.filetype;
        this.sizeFrom = builder.sizeFrom;
        this.sizeTo = builder.sizeTo;
        this.mtimeFrom = builder.mtimeFrom;
        this.mtimeTo = builder.mtimeTo;
        this.crtimeFrom = builder.crtimeFrom;
        this.crtimeTo = builder.crtimeTo;
        this.atimeFrom = builder.atimeFrom;
        this.atimeTo = builder.atimeTo;
        this.owner = builder.owner;
        this.group = builder.group;
    }

    public static Builder builder(String folderPath) {
        return new Builder(folderPath);
    }

    public static class Builder {

        private final List<String> folderPaths = new ArrayList<String>();
        private Boolean recursive;
        private String pattern;
        private String extension;
        private String filetype;
        private Long sizeFrom;
        private Long sizeTo;
        private Long mtimeFrom;
        private Long mtimeTo;
        private Long crtimeFrom;
        private Long crtimeTo;
        private Long atimeFrom;
        private Long atimeTo;
        private String owner;
        private String group;

        private Builder(String folderPath) {
            this.folderPaths.add(SynologyPath.normalize(folderPath));
        }

        /**
         * 追加一个搜索目录。Search 支持同时在多个目录下搜索。
         */
        public Builder addFolderPath(String folderPath) {
            this.folderPaths.add(SynologyPath.normalize(folderPath));
            return this;
        }

        public Builder recursive(Boolean recursive) {
            this.recursive = recursive;
            return this;
        }

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder extension(String extension) {
            this.extension = extension;
            return this;
        }

        public Builder filetype(String filetype) {
            this.filetype = filetype;
            return this;
        }

        public Builder filetype(FileTypeFilter filetype) {
            return filetype(filetype == null ? null : filetype.getValue());
        }

        public Builder sizeFrom(Long sizeFrom) {
            this.sizeFrom = sizeFrom;
            return this;
        }

        public Builder sizeTo(Long sizeTo) {
            this.sizeTo = sizeTo;
            return this;
        }

        public Builder mtimeFrom(Long mtimeFrom) {
            this.mtimeFrom = mtimeFrom;
            return this;
        }

        public Builder mtimeTo(Long mtimeTo) {
            this.mtimeTo = mtimeTo;
            return this;
        }

        public Builder crtimeFrom(Long crtimeFrom) {
            this.crtimeFrom = crtimeFrom;
            return this;
        }

        public Builder crtimeTo(Long crtimeTo) {
            this.crtimeTo = crtimeTo;
            return this;
        }

        public Builder atimeFrom(Long atimeFrom) {
            this.atimeFrom = atimeFrom;
            return this;
        }

        public Builder atimeTo(Long atimeTo) {
            this.atimeTo = atimeTo;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder group(String group) {
            this.group = group;
            return this;
        }

        public SearchStartRequest build() {
            return new SearchStartRequest(this);
        }
    }
}
