package io.github.shuzhuoi.synology.filestation.dirsize;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.DirSize start 方法的请求参数。
 * <p>
 * 计算一个或多个文件/文件夹的累计大小，属于非阻塞任务型 API。
 */
@Getter
public class DirSizeStartRequest {

    /**
     * 要计算大小的文件/文件夹路径列表，必须从共享目录开始。
     */
    private final List<String> paths;

    private DirSizeStartRequest(Builder builder) {
        this.paths = Collections.unmodifiableList(new ArrayList<String>(builder.paths));
    }

    public static Builder builder(String path) {
        return new Builder(path);
    }

    public static class Builder {

        private final List<String> paths = new ArrayList<String>();

        private Builder(String path) {
            this.paths.add(SynologyPath.normalize(path));
        }

        /**
         * 追加一个要计算大小的路径。
         */
        public Builder addPath(String path) {
            this.paths.add(SynologyPath.normalize(path));
            return this;
        }

        public DirSizeStartRequest build() {
            return new DirSizeStartRequest(this);
        }
    }
}
