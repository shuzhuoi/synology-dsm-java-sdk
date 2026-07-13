package io.github.shuzhuoi.synology.filestation.extract;

import io.github.shuzhuoi.synology.filestation.option.SortDirection;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

/**
 * SYNO.FileStation.Extract list 方法请求。
 * <p>
 * 用于读取压缩包内部条目，便于后续按 item_id 部分解压。
 */
@Getter
public class ExtractListRequest {

    /**
     * 待读取内部条目的压缩包路径。
     */
    private final String filePath;
    /**
     * 跳过多少条压缩包内部记录。
     */
    private final Integer offset;
    /**
     * 返回多少条记录，-1 表示返回全部。
     */
    private final Integer limit;
    /**
     * 排序字段：name、size、pack_size 或 mtime。
     */
    private final String sortBy;
    /**
     * 排序方向：asc 或 desc。
     */
    private final String sortDirection;
    /**
     * 文件名解码语言，默认使用 DSM Codepage 设置。
     */
    private final String codepage;
    /**
     * 压缩包密码。注意不要输出到日志。
     */
    private final String password;
    /**
     * 要读取的压缩包内部目录 ID，null 或 -1 表示根目录。
     */
    private final Integer itemId;

    private ExtractListRequest(Builder builder) {
        this.filePath = SynologyPath.normalize(builder.filePath);
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.codepage = builder.codepage;
        this.password = builder.password;
        this.itemId = builder.itemId;
    }

    public static Builder builder(String filePath) {
        return new Builder(filePath);
    }

    public static class Builder {
        private final String filePath;
        private Integer offset;
        private Integer limit;
        private String sortBy;
        private String sortDirection;
        private String codepage;
        private String password;
        private Integer itemId;

        private Builder(String filePath) {
            this.filePath = filePath;
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

        public Builder sortDirection(SortDirection sortDirection) {
            return sortDirection(sortDirection == null ? null : sortDirection.getValue());
        }

        public Builder codepage(String codepage) {
            this.codepage = codepage;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder itemId(Integer itemId) {
            this.itemId = itemId;
            return this;
        }

        public ExtractListRequest build() {
            return new ExtractListRequest(this);
        }
    }
}
