package io.github.shuzhuoi.synology.json;

import java.util.Map;

/**
 * Synology DSM 标准 JSON 响应编解码扩展点。
 * <p>
 * core 只依赖该接口，不感知 Jackson、Fastjson2 等具体 JSON 库。
 */
public interface SynologyJsonCodec {

    /**
     * 将完整 DSM 响应解码为指定 data 类型。
     *
     * @param body 完整 JSON 响应体
     * @param dataType data 节点目标类型
     * @param <T> 目标类型
     * @return 统一响应实体
     */
    <T> SynologyJsonResponse<T> decode(String body, Class<T> dataType);

    /**
     * 将完整 DSM 响应的 data 节点解码为有序 Map。
     *
     * @param body 完整 JSON 响应体
     * @param valueType Map 值类型
     * @param <T> Map 值类型
     * @return 统一响应实体，Map 实现必须保持 JSON 字段顺序
     */
    <T> SynologyJsonResponse<Map<String, T>> decodeMap(String body, Class<T> valueType);
}
