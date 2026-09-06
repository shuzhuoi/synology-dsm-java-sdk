package io.github.shuzhuoi.synology.auth;

import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * SYNO.API.Auth 登录的契约测试。
 * <p>
 * 通过记录型 HTTP fake 捕获登录请求，断言官方 v6 契约：
 * 基础参数（account/passwd/session/format），
 * 两步验证参数（otp_code），
 * 设备令牌参数（enable_device_token/device_name/device_id）按需下发、未配置时不携带，
 * 防止重构时破坏 DSM 登录契约。
 */
class AuthClientContractTest {

    private static final String SID = "sid-1";
    private static final String DID = "did-1";

    /**
     * 记录登录请求并固定返回含 sid 和 did 的成功响应。
     */
    private static class RecordingLoginHttpClient implements SynologyHttpClient {

        private final List<SynologyHttpRequest> requests = new ArrayList<SynologyHttpRequest>();

        @Override
        public SynologyHttpResponse execute(SynologyHttpRequest request) {
            requests.add(request);
            // did 只有携带 enable_device_token=yes 时 DSM 才返回，这里统一返回用于断言会话携带设备 ID。
            return new SynologyHttpResponse(
                    200,
                    null,
                    "{\"success\":true,\"data\":{\"sid\":\"" + SID + "\",\"did\":\"" + DID + "\"}}",
                    new ByteArrayInputStream(new byte[0])
            );
        }

        SynologyHttpRequest getLoginRequest() {
            return requests.get(requests.size() - 1);
        }
    }

    /**
     * 登录契约测试用最小 JSON fake：直接返回固定的 LoginResponse。
     */
    private static class LoginJsonCodec implements SynologyJsonCodec {

        @Override
        @SuppressWarnings("unchecked")
        public <T> SynologyJsonResponse<T> decode(String body, Class<T> dataType) {
            LoginResponse data = new LoginResponse();
            data.setSid(SID);
            data.setDid(DID);
            return new SynologyJsonResponse<T>(Boolean.TRUE, (T) data, null);
        }

        @Override
        public <T> SynologyJsonResponse<Map<String, T>> decodeMap(String body, Class<T> valueType) {
            return new SynologyJsonResponse<Map<String, T>>(Boolean.TRUE, null, null);
        }
    }

    @Test
    void plainLoginUsesAuthApiV6WithoutOtpParameters() {
        RecordingLoginHttpClient httpClient = new RecordingLoginHttpClient();
        AuthClient authClient = newAuthClient(httpClient, SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .account("demo")
                .password("secret")
                .build());

        SynologySession session = authClient.login();

        SynologyHttpRequest request = httpClient.getLoginRequest();
        assertEquals("http://nas:5000/webapi/auth.cgi", request.getUrl());
        assertEquals("SYNO.API.Auth", request.getParameters().get("api"));
        // 官方推荐 v6：otp_code 在 v3+ 可用，设备令牌参数在 v6+ 可用。
        assertEquals("6", request.getParameters().get("version"));
        assertEquals("login", request.getParameters().get("method"));
        assertEquals("demo", request.getParameters().get("account"));
        assertEquals("secret", request.getParameters().get("passwd"));
        assertEquals("FileStation", request.getParameters().get("session"));
        assertEquals("sid", request.getParameters().get("format"));
        // 未开启两步验证时不应携带任何 OTP 参数。
        assertFalse(request.getParameters().containsKey("otp_code"));
        assertFalse(request.getParameters().containsKey("enable_device_token"));
        assertFalse(request.getParameters().containsKey("device_name"));
        assertFalse(request.getParameters().containsKey("device_id"));

        assertEquals(SID, session.getSid());
        // 未申请设备令牌的会话不携带 deviceId。
        assertNull(session.getDeviceId());
    }

    @Test
    void loginWithOtpCodeSendsOtpParameter() {
        RecordingLoginHttpClient httpClient = new RecordingLoginHttpClient();
        AuthClient authClient = newAuthClient(httpClient, SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .account("demo")
                .password("secret")
                .otpCode("123456")
                .build());

        authClient.login();

        SynologyHttpRequest request = httpClient.getLoginRequest();
        assertEquals("123456", request.getParameters().get("otp_code"));
    }

    @Test
    void loginWithDeviceTokenSendsYesAndDeviceName() {
        RecordingLoginHttpClient httpClient = new RecordingLoginHttpClient();
        AuthClient authClient = newAuthClient(httpClient, SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .account("demo")
                .password("secret")
                .otpCode("123456")
                .enableDeviceToken(true)
                .deviceName("sdk-runner")
                .build());

        SynologySession session = authClient.login();

        SynologyHttpRequest request = httpClient.getLoginRequest();
        // 官方契约：enable_device_token 取值 yes/no，device_name 用于识别受信设备。
        assertEquals("yes", request.getParameters().get("enable_device_token"));
        assertEquals("sdk-runner", request.getParameters().get("device_name"));
        assertEquals("123456", request.getParameters().get("otp_code"));

        // 申请设备令牌成功后，会话应携带响应返回的 did，供持久化复用。
        assertEquals(DID, session.getDeviceId());
    }

    @Test
    void loginWithDeviceIdSkipsOtp() {
        RecordingLoginHttpClient httpClient = new RecordingLoginHttpClient();
        AuthClient authClient = newAuthClient(httpClient, SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .account("demo")
                .password("secret")
                .deviceId(DID)
                .build());

        authClient.login();

        SynologyHttpRequest request = httpClient.getLoginRequest();
        // 携带受信设备 ID 时不需要动态验证码。
        assertEquals(DID, request.getParameters().get("device_id"));
        assertFalse(request.getParameters().containsKey("otp_code"));
    }

    private AuthClient newAuthClient(RecordingLoginHttpClient httpClient, SynologyDsmConfig config) {
        SynologyApiExecutor executor = new SynologyApiExecutor(config, httpClient, new LoginJsonCodec());
        return new AuthClient(config, executor);
    }
}
