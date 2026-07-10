package io.github.shuzhuoi.synology.filestation.sharing;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Sharing create 方法请求参数。
 */
@Getter
public class SharingCreateRequest {

    private final List<String> paths;
    private final String password;
    private final String dateExpired;
    private final String dateAvailable;

    private SharingCreateRequest(Builder builder) {
        this.paths = Collections.unmodifiableList(new ArrayList<String>(builder.paths));
        this.password = builder.password;
        this.dateExpired = builder.dateExpired;
        this.dateAvailable = builder.dateAvailable;
    }

    public static Builder builder(String path) {
        return new Builder(path);
    }

    public static class Builder {
        private final List<String> paths = new ArrayList<String>();
        private String password;
        private String dateExpired;
        private String dateAvailable;

        private Builder(String path) {
            this.paths.add(SynologyPath.normalize(path));
        }

        public Builder addPath(String path) {
            this.paths.add(SynologyPath.normalize(path));
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder dateExpired(String dateExpired) {
            this.dateExpired = dateExpired;
            return this;
        }

        public Builder dateAvailable(String dateAvailable) {
            this.dateAvailable = dateAvailable;
            return this;
        }

        public SharingCreateRequest build() {
            return new SharingCreateRequest(this);
        }
    }
}
