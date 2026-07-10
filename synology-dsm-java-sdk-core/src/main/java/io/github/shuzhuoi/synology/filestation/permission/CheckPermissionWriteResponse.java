package io.github.shuzhuoi.synology.filestation.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckPermissionWriteResponse {

    /**
     * DSM 返回 success=true 时表示写入权限检查通过；无权限会由统一执行器抛出异常。
     */
    private final boolean allowed;
}
