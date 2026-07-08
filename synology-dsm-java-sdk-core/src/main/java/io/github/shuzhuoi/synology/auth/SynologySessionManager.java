package io.github.shuzhuoi.synology.auth;

import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyAuthException;

/**
 * File Station 会话管理器。
 * <p>
 * 当前实现会在首次需要认证接口时自动登录并缓存 SID，避免每次文件操作都重新登录。
 */
public class SynologySessionManager {

    private final SynologyDsmConfig config;
    private final AuthClient authClient;
    private SynologySession currentSession;

    public SynologySessionManager(SynologyDsmConfig config, AuthClient authClient) {
        this.config = config;
        this.authClient = authClient;
    }

    public synchronized SynologySession currentSession() {
        // 懒登录：真正需要 SID 时才调用登录接口。
        if (currentSession == null) {
            if (!config.isAutoLogin()) {
                throw new SynologyAuthException("session is not initialized and autoLogin is disabled");
            }
            currentSession = authClient.login();
        }
        return currentSession;
    }

    public synchronized SynologySession refresh() {
        // 预留给后续自动刷新会话使用。
        currentSession = authClient.login();
        return currentSession;
    }

    public synchronized void clear() {
        currentSession = null;
    }

    public synchronized LogoutResponse logout() {
        SynologySession session = currentSession;
        currentSession = null;
        return authClient.logout(session == null ? null : session.getSid());
    }
}
