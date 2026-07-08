package io.github.shuzhuoi.synology.filestation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynologyFileAdditional {

    /**
     * 文件大小，单位字节。
     */
    private Long size;
    /**
     * 文件所有者信息。
     */
    private SynologyOwner owner;
    /**
     * 文件时间信息，通常是 Linux 时间戳秒。
     */
    private SynologyTime time;
    /**
     * 文件权限信息。
     */
    private SynologyPermission perm;
    /**
     * 文件扩展名或类型。
     */
    private String type;

    /**
     * DSM 底层真实路径，只有请求 additional=real_path 时返回。
     */
    @JsonProperty("real_path")
    private String realPath;

    /**
     * 挂载点类型，只有请求 additional=mount_point_type 时返回。
     */
    @JsonProperty("mount_point_type")
    private String mountPointType;
}
