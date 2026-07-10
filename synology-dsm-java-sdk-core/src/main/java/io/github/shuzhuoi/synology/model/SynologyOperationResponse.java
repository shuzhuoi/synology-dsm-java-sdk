package io.github.shuzhuoi.synology.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DSM 空成功响应的通用实体。
 * <p>
 * 部分 API 只返回 success=true 而没有 data，本实体用于保持公开方法仍然返回强类型对象。
 */
@Getter
@AllArgsConstructor
public class SynologyOperationResponse {

    /**
     * 操作是否成功。能构造该对象表示 DSM 已返回 success=true。
     */
    private final boolean success;
}
