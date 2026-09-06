package io.github.shuzhuoi.synology.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String sid;

    /**
     * 设备 ID（did）。登录时携带 enable_device_token=yes 才会返回，
     * 持久化后可作为下次登录的 deviceId，跳过两步验证。
     */
    private String did;

    /**
     * 防 CSRF 的 SynoToken。当前 SDK 使用 _sid 会话方式调用接口，仅保留字段用于解码。
     */
    private String synotoken;
}
