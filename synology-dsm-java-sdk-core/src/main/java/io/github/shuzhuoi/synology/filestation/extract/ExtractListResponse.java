package io.github.shuzhuoi.synology.filestation.extract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.FileStation.Extract list 方法响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class ExtractListResponse {

    /**
     * 压缩包内部条目总数。
     */
    private Integer total;
    /**
     * 当前返回的压缩包内部条目。
     */
    private List<ArchiveItem> items;
}
