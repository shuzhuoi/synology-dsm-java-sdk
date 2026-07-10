package io.github.shuzhuoi.synology.filestation.compress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.FileStation.Compress status 方法响应。
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompressStatusResponse {

    /**
     * 压缩任务是否完成。
     */
    private Boolean finished;
    /**
     * 目标压缩包路径。
     */
    @JsonProperty("dest_file_path")
    private String destFilePath;
}
