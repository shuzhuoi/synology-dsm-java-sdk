package io.github.shuzhuoi.synology.filestation.thumb;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

/**
 * SYNO.FileStation.Thumb get 方法请求参数。
 */
@Getter
public class ThumbGetRequest {

    private final String path;
    private final String size;
    private final Integer rotate;

    private ThumbGetRequest(Builder builder) {
        this.path = SynologyPath.normalize(builder.path);
        this.size = builder.size;
        this.rotate = builder.rotate;
    }

    public static Builder builder(String path) {
        return new Builder(path);
    }

    public static class Builder {
        private final String path;
        private String size;
        private Integer rotate;

        private Builder(String path) {
            this.path = path;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }

        public Builder rotate(Integer rotate) {
            this.rotate = rotate;
            return this;
        }

        public ThumbGetRequest build() {
            return new ThumbGetRequest(this);
        }
    }
}
