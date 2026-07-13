package io.github.shuzhuoi.synology.filestation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyFile {

    /**
     * 文件或文件夹名称。
     */
    private String name;
    /**
     * DSM 中的完整路径。
     */
    private String path;
    /**
     * 是否为文件夹。字段名沿用 DSM 返回的 isdir。
     */
    private Boolean isdir;
    /**
     * additional 参数请求到的扩展信息。
     */
    private SynologyFileAdditional additional;
}
