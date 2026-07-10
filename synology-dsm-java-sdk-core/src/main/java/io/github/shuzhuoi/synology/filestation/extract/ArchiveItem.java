package io.github.shuzhuoi.synology.filestation.extract;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 压缩包内部条目。
 * <p>
 * 对应 SYNO.FileStation.Extract list 返回的 items 列表。
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveItem {

    /**
     * 压缩包内部条目 ID，后续可传给 start 的 item_id 做部分解压。
     */
    @JsonProperty("item_id")
    @JsonAlias("itemid")
    private Integer itemId;
    /**
     * 压缩包内部文件名。
     */
    private String name;
    /**
     * 原始文件大小，单位字节。
     */
    private Long size;
    /**
     * 压缩后大小，单位字节。
     */
    @JsonProperty("pack_size")
    private Long packSize;
    /**
     * 最后修改时间，官方以字符串形式返回。
     */
    private String mtime;
    /**
     * 压缩包内部相对路径。
     */
    private String path;
    /**
     * 是否为目录。
     */
    @JsonProperty("is_dir")
    private Boolean dir;
}
