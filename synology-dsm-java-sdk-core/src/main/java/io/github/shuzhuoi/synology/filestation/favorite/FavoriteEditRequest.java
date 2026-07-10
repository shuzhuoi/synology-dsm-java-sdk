package io.github.shuzhuoi.synology.filestation.favorite;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

/**
 * SYNO.FileStation.Favorite edit 方法请求参数。
 */
@Getter
public class FavoriteEditRequest {

    private final String path;
    private final String name;

    private FavoriteEditRequest(Builder builder) {
        this.path = SynologyPath.normalize(builder.path);
        this.name = builder.name;
    }

    public static Builder builder(String path, String name) {
        return new Builder(path, name);
    }

    public static class Builder {
        private final String path;
        private final String name;

        private Builder(String path, String name) {
            this.path = path;
            this.name = name;
        }

        public FavoriteEditRequest build() {
            return new FavoriteEditRequest(this);
        }
    }
}
