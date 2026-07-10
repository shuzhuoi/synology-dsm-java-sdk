package io.github.shuzhuoi.synology.filestation.extract;

import io.github.shuzhuoi.synology.util.SynologyPath;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SYNO.FileStation.Extract start 方法请求。
 * <p>
 * 解压是非阻塞任务，start 返回 taskid 后需要继续 status 或 wait。
 */
@Getter
public class ExtractStartRequest {

    /**
     * 待解压的压缩包路径。
     */
    private final String filePath;
    /**
     * 解压目标目录路径。
     */
    private final String destFolderPath;
    /**
     * 目标文件存在时是否覆盖。
     */
    private final Boolean overwrite;
    /**
     * 是否保留压缩包内部目录结构。
     */
    private final Boolean keepDir;
    /**
     * 是否按压缩包名创建子目录。
     */
    private final Boolean createSubfolder;
    /**
     * 文件名解码语言，默认使用 DSM Codepage 设置。
     */
    private final String codepage;
    /**
     * 压缩包密码。注意不要输出到日志。
     */
    private final String password;
    /**
     * 只解压指定内部条目；为空时解压全部。
     */
    private final List<Integer> itemIds;

    private ExtractStartRequest(Builder builder) {
        this.filePath = SynologyPath.normalize(builder.filePath);
        this.destFolderPath = SynologyPath.normalize(builder.destFolderPath);
        this.overwrite = builder.overwrite;
        this.keepDir = builder.keepDir;
        this.createSubfolder = builder.createSubfolder;
        this.codepage = builder.codepage;
        this.password = builder.password;
        this.itemIds = Collections.unmodifiableList(new ArrayList<Integer>(builder.itemIds));
    }

    public static Builder builder(String filePath, String destFolderPath) {
        return new Builder(filePath, destFolderPath);
    }

    public static class Builder {
        private final String filePath;
        private final String destFolderPath;
        private Boolean overwrite;
        private Boolean keepDir;
        private Boolean createSubfolder;
        private String codepage;
        private String password;
        private final List<Integer> itemIds = new ArrayList<Integer>();

        private Builder(String filePath, String destFolderPath) {
            this.filePath = filePath;
            this.destFolderPath = destFolderPath;
        }

        public Builder overwrite(Boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public Builder keepDir(Boolean keepDir) {
            this.keepDir = keepDir;
            return this;
        }

        public Builder createSubfolder(Boolean createSubfolder) {
            this.createSubfolder = createSubfolder;
            return this;
        }

        public Builder codepage(String codepage) {
            this.codepage = codepage;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder addItemId(Integer itemId) {
            if (itemId != null) {
                this.itemIds.add(itemId);
            }
            return this;
        }

        public ExtractStartRequest build() {
            return new ExtractStartRequest(this);
        }
    }
}
