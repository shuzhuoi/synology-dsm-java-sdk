package io.github.shuzhuoi.synology.filestation.dirsize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.FileStation.DirSize status 方法的响应。
 * <p>
 * total_size 为累计字节数，可能很大，使用 Long 承载。
 * 注意：DSM 返回的字段名为下划线风格，ObjectMapper 未启用 SNAKE_CASE，
 * 因此这里通过 @JsonProperty 显式映射，与项目中 SynologyFileAdditional 等类的做法一致。
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirSizeStatusResponse {

    /**
     * 任务是否完成。
     */
    private Boolean finished;
    /**
     * 查询路径下的目录数。
     */
    @JsonProperty("num_dir")
    private Integer numDir;
    /**
     * 查询路径下的文件数。
     */
    @JsonProperty("num_file")
    private Integer numFile;
    /**
     * 查询路径下的累计字节数。
     */
    @JsonProperty("total_size")
    private Long totalSize;
}
