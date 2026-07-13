package io.github.shuzhuoi.synology.filestation.sharing;

import io.github.shuzhuoi.synology.filestation.option.SortDirection;

import lombok.Getter;

/**
 * SYNO.FileStation.Sharing list 方法请求参数。
 */
@Getter
public class SharingListRequest {

    private final Integer offset;
    private final Integer limit;
    private final String sortBy;
    private final String sortDirection;
    private final Boolean forceClean;

    private SharingListRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.forceClean = builder.forceClean;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer offset;
        private Integer limit;
        private String sortBy;
        private String sortDirection;
        private Boolean forceClean;

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

        public Builder forceClean(Boolean forceClean) {
            this.forceClean = forceClean;
            return this;
        }

        public SharingListRequest build() {
            return new SharingListRequest(this);
        }
    }
}
