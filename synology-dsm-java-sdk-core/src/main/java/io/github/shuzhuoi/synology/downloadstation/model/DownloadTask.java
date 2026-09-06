package io.github.shuzhuoi.synology.downloadstation.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 下载任务实体。
 * <p>
 * size 等字段可能超出 int 范围，统一使用 Long 承载；
 * status 保持字符串，需要枚举时通过 {@link DownloadTaskStatus#fromValue(String)} 转换。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadTask {

    /**
     * 任务 ID，形如 dbid_001，是后续 getinfo/delete/pause 等操作的句柄。
     */
    private String id;
    /**
     * 任务类型：bt、http、ftp、nzb、eMule 等。
     */
    private String type;
    /**
     * 创建任务的用户。
     */
    private String username;
    /**
     * 任务标题，通常是文件名或链接标题。
     */
    private String title;
    /**
     * 任务总字节数。
     */
    private Long size;
    /**
     * 任务状态字符串，取值见 {@link DownloadTaskStatus}。
     */
    private String status;
    /**
     * 状态附加信息，例如哈希校验进度，无则为 null。
     */
    @SynologyJsonProperty("status_extra")
    private Map<String, Object> statusExtra;
    /**
     * 扩展信息块，只有请求 additional 时才返回对应内容。
     */
    private DownloadTaskAdditional additional;
}
