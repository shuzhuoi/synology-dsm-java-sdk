package io.github.shuzhuoi.synology.downloadstation.taskfile;

import io.github.shuzhuoi.synology.downloadstation.model.DownloadTaskFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.DownloadStation2.Task.File get 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskFileListResponse {

    /**
     * 文件总数。
     */
    private Integer total;
    /**
     * 当前页起始序号。
     */
    private Integer offset;
    /**
     * 任务内文件列表。
     */
    private List<DownloadTaskFile> files;
}
