package io.github.shuzhuoi.synology.filestation.list;

import io.github.shuzhuoi.synology.model.Additional;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ListSharesRequest {

    private final Integer offset;
    private final Integer limit;
    private final String sortBy;
    private final String sortDirection;
    private final Boolean onlyWritable;
    private final List<Additional> additional;

    private ListSharesRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.onlyWritable = builder.onlyWritable;
        this.additional = Collections.unmodifiableList(new ArrayList<Additional>(builder.additional));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer offset;
        private Integer limit;
        private String sortBy;
        private String sortDirection;
        private Boolean onlyWritable;
        private List<Additional> additional = new ArrayList<Additional>();

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

        public Builder onlyWritable(Boolean onlyWritable) {
            this.onlyWritable = onlyWritable;
            return this;
        }

        public Builder addAdditional(Additional additional) {
            if (additional != null) {
                this.additional.add(additional);
            }
            return this;
        }

        public ListSharesRequest build() {
            return new ListSharesRequest(this);
        }
    }
}
