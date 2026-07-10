package io.github.shuzhuoi.synology.filestation.sharing;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Sharing edit 方法请求参数。
 */
@Getter
public class SharingEditRequest {

    private final List<String> ids;
    private final String password;
    private final String dateExpired;
    private final String dateAvailable;

    private SharingEditRequest(Builder builder) {
        this.ids = Collections.unmodifiableList(new ArrayList<String>(builder.ids));
        this.password = builder.password;
        this.dateExpired = builder.dateExpired;
        this.dateAvailable = builder.dateAvailable;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final List<String> ids = new ArrayList<String>();
        private String password;
        private String dateExpired;
        private String dateAvailable;

        private Builder(String id) {
            this.ids.add(id);
        }

        public Builder addId(String id) {
            this.ids.add(id);
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

        public SharingEditRequest build() {
            return new SharingEditRequest(this);
        }
    }
}
