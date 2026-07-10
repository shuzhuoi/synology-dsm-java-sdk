package io.github.shuzhuoi.synology.filestation.extract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.FileStation.Extract status 方法响应。
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtractStatusResponse {

    /**
     * 解压任务是否完成。
     */
    private Boolean finished;
    /**
     * 解压进度，官方文档中为 0 到 1 的小数。
     */
    private Double progress;
    /**
     * 解压目标目录。
     */
    @JsonProperty("dest_folder_path")
    private String destFolderPath;
}
