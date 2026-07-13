package io.github.shuzhuoi.synology.filestation.dirsize;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.FileStation.DirSize status 方法的响应。
 * <p>
 * total_size 为累计字节数，可能很大，使用 Long 承载。
 * 注意：DSM 返回的字段名为下划线风格，因此通过 SDK 中立注解显式映射。
 */
@Getter
@Setter
@NoArgsConstructor
public class DirSizeStatusResponse {

    /**
     * 任务是否完成。
     */
    private Boolean finished;
    /**
     * 查询路径下的目录数。
     */
    @SynologyJsonProperty("num_dir")
    private Integer numDir;
    /**
     * 查询路径下的文件数。
     */
    @SynologyJsonProperty("num_file")
    private Integer numFile;
    /**
     * 查询路径下的累计字节数。
     */
    @SynologyJsonProperty("total_size")
    private Long totalSize;
}
