package io.github.shuzhuoi.synology.docker.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * SYNO.Docker.Registry search 方法的响应。
 * <p>
 * 注意该接口的 data 节点内部再嵌一层 data 数组（官方原始结构），
 * 本模型对应内层节点：data 为搜索结果列表。
 */
@Getter
@Setter
@NoArgsConstructor
public class DockerRegistrySearchResponse {

    /**
     * 搜索结果列表。
     */
    private List<DockerRegistrySearchResult> data;
    /**
     * 结果总数。
     */
    private Integer total;
    /**
     * 当前偏移量。
     */
    private Integer offset;
    /**
     * 当前查询上限。
     */
    private Integer limit;
    /**
     * 分页大小。
     */
    @SynologyJsonProperty("page_size")
    private Integer pageSize;
}
