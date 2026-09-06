package io.github.shuzhuoi.synology.auth;

import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyAuthException;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.API.Auth 客户端，负责登录和登出。
 */
public class AuthClient {

    private final SynologyDsmConfig config;
    private final SynologyApiExecutor executor;

    public AuthClient(SynologyDsmConfig config, SynologyApiExecutor executor) {
        this.config = config;
        this.executor = executor;
    }

    public SynologySession login() {
        // 使用 format=sid，让 SID 出现在 JSON 响应里，后续通过 _sid 参数调用接口。
        if (isBlank(config.getAccount()) || isBlank(config.getPassword())) {
            throw new SynologyAuthException("account and password are required for login");
        }

        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("account", config.getAccount());
        parameters.put("passwd", config.getPassword());
        parameters.put("session", config.getSessionName());
        parameters.put("format", "sid");
        // 开启两步验证的账号必须携带动态验证码，否则 DSM 返回 403/404 错误码。
        if (!isBlank(config.getOtpCode())) {
            parameters.put("otp_code", config.getOtpCode());
        }
        // 申请设备令牌：登录成功后响应返回 did，持久化后可免 OTP 登录。
        if (config.isEnableDeviceToken()) {
            parameters.put("enable_device_token", "yes");
        }
        if (!isBlank(config.getDeviceName())) {
            parameters.put("device_name", config.getDeviceName());
        }
        // 携带上次登录返回的 did，DSM 跳过两步验证。
        if (!isBlank(config.getDeviceId())) {
            parameters.put("device_id", config.getDeviceId());
        }

        // 使用 v6：otp_code 在 v3+ 可用，enable_device_token/device_id 在 v6+ 可用。
        LoginResponse response = executor.getPublic(
                "auth.cgi",
                "SYNO.API.Auth",
                6,
                "login",
                parameters,
                LoginResponse.class
        );
        if (response == null || isBlank(response.getSid())) {
            throw new SynologyAuthException("login succeeded but sid is blank");
        }
        return new SynologySession(response.getSid(), config.getSessionName(), new Date(), response.getDid());
    }

    public LogoutResponse logout(String sid) {
        // 如果传入 sid，则登出指定 FileStation 会话；为空时按 DSM 默认会话规则处理。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("session", config.getSessionName());
        if (!isBlank(sid)) {
            parameters.put("_sid", sid);
        }
        executor.getPublic("auth.cgi", "SYNO.API.Auth", 1, "logout", parameters, Object.class);
        return new LogoutResponse(true);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}
