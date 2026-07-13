package io.github.shuzhuoi.synology.filestation.search;

import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.FileStation.Search list 方法的响应。
 * <p>
 * files 字段中的文件对象结构与 SYNO.FileStation.List 一致。
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchListResponse {

    /**
     * 匹配文件总数。
     */
    private Integer total;
    /**
     * 当前返回结果的偏移量。
     */
    private Integer offset;
    /**
     * 搜索任务是否已完成。为 false 时表示仍在搜索，需继续轮询 list。
     */
    private Boolean finished;
    /**
     * 匹配的文件列表。
     */
    private List<SynologyFile> files;
}
