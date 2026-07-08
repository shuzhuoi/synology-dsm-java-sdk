package io.github.shuzhuoi.synology.filestation.list;

import io.github.shuzhuoi.synology.model.Additional;
import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class GetFileInfoRequest {

    private final List<String> paths;
    private final List<Additional> additional;

    private GetFileInfoRequest(Builder builder) {
        List<String> normalized = new ArrayList<String>();
        for (String path : builder.paths) {
            normalized.add(SynologyPath.normalize(path));
        }
        this.paths = Collections.unmodifiableList(normalized);
        this.additional = Collections.unmodifiableList(new ArrayList<Additional>(builder.additional));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> paths = new ArrayList<String>();
        private List<Additional> additional = new ArrayList<Additional>();

        public Builder addPath(String path) {
            this.paths.add(path);
            return this;
        }

        public Builder addAdditional(Additional additional) {
            if (additional != null) {
                this.additional.add(additional);
            }
            return this;
        }

        public GetFileInfoRequest build() {
            if (paths.isEmpty()) {
                throw new IllegalArgumentException("at least one path is required");
            }
            return new GetFileInfoRequest(this);
        }
    }
}
