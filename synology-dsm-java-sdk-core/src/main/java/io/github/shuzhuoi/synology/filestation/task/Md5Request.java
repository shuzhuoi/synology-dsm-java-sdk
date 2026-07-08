package io.github.shuzhuoi.synology.filestation.task;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

@Getter
public class Md5Request {

    private final String filePath;

    private Md5Request(Builder builder) {
        this.filePath = SynologyPath.normalize(builder.filePath);
    }

    public static Builder builder(String filePath) {
        return new Builder(filePath);
    }

    public static class Builder {
        private final String filePath;

        private Builder(String filePath) {
            this.filePath = filePath;
        }

        public Md5Request build() {
            return new Md5Request(this);
        }
    }
}
