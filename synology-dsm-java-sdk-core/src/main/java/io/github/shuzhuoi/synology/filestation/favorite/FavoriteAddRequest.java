package io.github.shuzhuoi.synology.filestation.favorite;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

/**
 * SYNO.FileStation.Favorite add 方法请求参数。
 */
@Getter
public class FavoriteAddRequest {

    private final String path;
    private final String name;
    private final Integer index;

    private FavoriteAddRequest(Builder builder) {
        this.path = SynologyPath.normalize(builder.path);
        this.name = builder.name;
        this.index = builder.index;
    }

    public static Builder builder(String path, String name) {
        return new Builder(path, name);
    }

    public static class Builder {
        private final String path;
        private final String name;
        private Integer index;

        private Builder(String path, String name) {
            this.path = path;
            this.name = name;
        }

        public Builder index(Integer index) {
            this.index = index;
            return this;
        }

        public FavoriteAddRequest build() {
            return new FavoriteAddRequest(this);
        }
    }
}
