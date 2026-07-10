package io.github.shuzhuoi.synology.filestation.virtualfolder;

import io.github.shuzhuoi.synology.model.Additional;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.VirtualFolder list 方法请求参数。
 */
@Getter
public class VirtualFolderListRequest {

    private final String type;
    private final Integer offset;
    private final Integer limit;
    private final String sortBy;
    private final String sortDirection;
    private final List<Additional> additional;

    private VirtualFolderListRequest(Builder builder) {
        this.type = builder.type;
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.additional = Collections.unmodifiableList(new ArrayList<Additional>(builder.additional));
    }

    public static Builder builder(String type) {
        return new Builder(type);
    }

    public static class Builder {
        private final String type;
        private Integer offset;
        private Integer limit;
        private String sortBy;
        private String sortDirection;
        private List<Additional> additional = new ArrayList<Additional>();

        private Builder(String type) {
            this.type = type;
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

        public Builder addAdditional(Additional additional) {
            if (additional != null) {
                this.additional.add(additional);
            }
            return this;
        }

        public VirtualFolderListRequest build() {
            return new VirtualFolderListRequest(this);
        }
    }
}
