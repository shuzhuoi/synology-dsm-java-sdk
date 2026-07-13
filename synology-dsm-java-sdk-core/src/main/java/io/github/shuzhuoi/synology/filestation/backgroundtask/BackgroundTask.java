package io.github.shuzhuoi.synology.filestation.backgroundtask;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 后台任务对象。
 * <p>
 * 对应 SYNO.FileStation.BackgroundTask list 方法返回的 tasks 数组中的单个元素，
 * 涵盖 copy、move、delete、compress、extract 等非阻塞任务的状态快照。
 * 注意：DSM 返回的部分字段为下划线风格，因此通过 SDK 中立注解显式映射。
 */
@Getter
@Setter
@NoArgsConstructor
public class BackgroundTask {

    /**
     * 任务所属的 API 名称，例如 SYNO.FileStation.CopyMove。
     */
    private String api;
    /**
     * 请求时使用的 API 版本。
     */
    private String version;
    /**
     * 请求时使用的方法，通常为 start。
     */
    private String method;
    /**
     * 后台任务唯一 ID。
     */
    private String taskid;
    /**
     * 任务是否已完成。
     */
    private Boolean finished;
    /**
     * start 方法的请求参数，官方为 JSON-Style Object，包含 path 数组等嵌套结构。
     * 使用 JDK Map 保留嵌套对象、列表、数字、布尔值和 null，避免公开具体 JSON 库类型。
     */
    private Map<String, Object> params;
    /**
     * 任务处理的路径。
     */
    private String path;
    /**
     * 已处理的文件数。
     */
    @SynologyJsonProperty("processed_num")
    private Integer processedNum;
    /**
     * 已处理的字节数。
     */
    @SynologyJsonProperty("processed_size")
    private Long processedSize;
    /**
     * 当前正在处理的文件路径。
     */
    @SynologyJsonProperty("processing_path")
    private String processingPath;
    /**
     * 总数或总字节数。API 不支持时官方返回 -1。
     */
    private Long total;
    /**
     * 进度，0 到 1 的小数。API 不支持时官方返回 0。
     */
    private Double progress;
    /**
     * 任务创建时间，Linux 秒级时间戳。
     */
    private Long crtime;
}
