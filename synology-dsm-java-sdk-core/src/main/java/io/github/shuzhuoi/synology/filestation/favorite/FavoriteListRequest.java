package io.github.shuzhuoi.synology.filestation.favorite;

import io.github.shuzhuoi.synology.model.Additional;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Favorite list 方法请求参数。
 */
@Getter
public class FavoriteListRequest {

    private final Integer offset;
    private final Integer limit;
    private final String statusFilter;
    private final List<Additional> additional;

    private FavoriteListRequest(Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.statusFilter = builder.statusFilter;
        this.additional = Collections.unmodifiableList(new ArrayList<Additional>(builder.additional));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer offset;
        private Integer limit;
        private String statusFilter;
        private List<Additional> additional = new ArrayList<Additional>();

        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder statusFilter(String statusFilter) {
            this.statusFilter = statusFilter;
            return this;
        }

        public Builder addAdditional(Additional additional) {
            if (additional != null) {
                this.additional.add(additional);
            }
            return this;
        }

        public FavoriteListRequest build() {
            return new FavoriteListRequest(this);
        }
    }
}
