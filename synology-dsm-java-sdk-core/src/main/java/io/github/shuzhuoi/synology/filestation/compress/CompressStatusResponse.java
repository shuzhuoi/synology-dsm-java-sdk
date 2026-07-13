package io.github.shuzhuoi.synology.filestation.compress;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.FileStation.Compress status 方法响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class CompressStatusResponse {

    /**
     * 压缩任务是否完成。
     */
    private Boolean finished;
    /**
     * 目标压缩包路径。
     */
    @SynologyJsonProperty("dest_file_path")
    private String destFilePath;
}
