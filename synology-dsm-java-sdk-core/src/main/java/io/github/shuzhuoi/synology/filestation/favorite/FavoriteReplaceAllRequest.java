package io.github.shuzhuoi.synology.filestation.favorite;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Favorite replace_all 方法请求参数。
 */
@Getter
public class FavoriteReplaceAllRequest {

    private final List<String> paths;
    private final List<String> names;

    private FavoriteReplaceAllRequest(Builder builder) {
        if (builder.paths.size() != builder.names.size()) {
            throw new IllegalArgumentException("favorite paths size must equal names size");
        }
        this.paths = Collections.unmodifiableList(new ArrayList<String>(builder.paths));
        this.names = Collections.unmodifiableList(new ArrayList<String>(builder.names));
    }

    public static Builder builder(String path, String name) {
        return new Builder(path, name);
    }

    public static class Builder {
        private final List<String> paths = new ArrayList<String>();
        private final List<String> names = new ArrayList<String>();

        private Builder(String path, String name) {
            addFavorite(path, name);
        }

        public Builder addFavorite(String path, String name) {
            this.paths.add(SynologyPath.normalize(path));
            this.names.add(name);
            return this;
        }

        public FavoriteReplaceAllRequest build() {
            return new FavoriteReplaceAllRequest(this);
        }
    }
}
