package io.github.shuzhuoi.synology.filestation.compress;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Compress start 方法请求。
 * <p>
 * 压缩是非阻塞任务，start 返回 taskid 后需要继续 status 或 wait。
 */
@Getter
public class CompressStartRequest {

    /**
     * 待压缩的文件/目录路径列表。
     */
    private final List<String> paths;
    /**
     * 目标压缩包完整路径，包含文件名。
     */
    private final String destFilePath;
    /**
     * 压缩等级：moderate、store、fastest、best。
     */
    private final String level;
    /**
     * 压缩模式：add、update、refreshen、synchronize。
     */
    private final String mode;
    /**
     * 压缩格式：zip 或 7z。
     */
    private final String format;
    /**
     * 压缩包密码。注意不要输出到日志。
     */
    private final String password;

    private CompressStartRequest(Builder builder) {
        this.paths = Collections.unmodifiableList(new ArrayList<String>(builder.paths));
        this.destFilePath = SynologyPath.normalize(builder.destFilePath);
        this.level = builder.level;
        this.mode = builder.mode;
        this.format = builder.format;
        this.password = builder.password;
    }

    public static Builder builder(String path, String destFilePath) {
        return new Builder(path, destFilePath);
    }

    public static class Builder {
        private final List<String> paths = new ArrayList<String>();
        private final String destFilePath;
        private String level;
        private String mode;
        private String format;
        private String password;

        private Builder(String path, String destFilePath) {
            this.paths.add(SynologyPath.normalize(path));
            this.destFilePath = destFilePath;
        }

        /**
         * 追加一个待压缩路径。
         */
        public Builder addPath(String path) {
            this.paths.add(SynologyPath.normalize(path));
            return this;
        }

        public Builder level(String level) {
            this.level = level;
            return this;
        }

        public Builder level(CompressLevel level) {
            return level(level == null ? null : level.getValue());
        }

        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public Builder mode(CompressMode mode) {
            return mode(mode == null ? null : mode.getValue());
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder format(CompressFormat format) {
            return format(format == null ? null : format.getValue());
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public CompressStartRequest build() {
            return new CompressStartRequest(this);
        }
    }
}
