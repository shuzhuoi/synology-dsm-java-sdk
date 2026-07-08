package io.github.shuzhuoi.synology.filestation.backgroundtask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 后台任务对象。
 * <p>
 * 对应 SYNO.FileStation.BackgroundTask list 方法返回的 tasks 数组中的单个元素，
 * 涵盖 copy、move、delete、compress、extract 等非阻塞任务的状态快照。
 * 注意：DSM 返回的部分字段为下划线风格，ObjectMapper 未启用 SNAKE_CASE，
 * 因此这里通过 @JsonProperty 显式映射，与项目中 SynologyFileAdditional 等类的做法一致。
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
     * 这里保留原始 JSON，调用方按需自行解析，避免丢失字段。
     */
    private JsonNode params;
    /**
     * 任务处理的路径。
     */
    private String path;
    /**
     * 已处理的文件数。
     */
    @JsonProperty("processed_num")
    private Integer processedNum;
    /**
     * 已处理的字节数。
     */
    @JsonProperty("processed_size")
    private Long processedSize;
    /**
     * 当前正在处理的文件路径。
     */
    @JsonProperty("processing_path")
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
