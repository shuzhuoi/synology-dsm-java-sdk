package io.github.shuzhuoi.synology.auth;

import io.github.shuzhuoi.synology.auth.store.InMemorySynologySessionStore;
import io.github.shuzhuoi.synology.auth.store.SynologyCachedSession;
import io.github.shuzhuoi.synology.auth.store.SynologySessionKey;
import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyAuthException;

/**
 * File Station 会话管理器。
 * <p>
 * 当前实现会在首次需要认证接口时自动登录，并通过 SynologySessionStore 缓存 SID，
 * 避免每次文件操作都重新登录。
 */
public class SynologySessionManager {

    private final SynologyDsmConfig config;
    private final AuthClient authClient;
    private final SynologySessionStore sessionStore;
    private final SynologySessionKey sessionKey;

    public SynologySessionManager(SynologyDsmConfig config, AuthClient authClient) {
        this(config, authClient, new InMemorySynologySessionStore());
    }

    public SynologySessionManager(SynologyDsmConfig config, AuthClient authClient, SynologySessionStore sessionStore) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }
        if (sessionStore == null) {
            throw new IllegalArgumentException("sessionStore must not be null");
        }
        this.config = config;
        this.authClient = authClient;
        this.sessionStore = sessionStore;
        this.sessionKey = SynologySessionKey.fromConfig(config);
    }

    public synchronized SynologySession currentSession() {
        // 优先读取缓存，允许不同 SynologyDsmClient 实例共享同一个可替换 SessionStore。
        SynologyCachedSession cachedSession = sessionStore.get(sessionKey);
        if (cachedSession != null) {
            return cachedSession.getSession();
        }
        if (!config.isAutoLogin()) {
            throw new SynologyAuthException("session is not initialized and autoLogin is disabled");
        }
        SynologySession session = login();
        sessionStore.put(sessionKey, SynologyCachedSession.of(session));
        return session;
    }

    public synchronized SynologySession refresh() {
        // DSM 返回 106/107/119 时主动重新登录，并覆盖缓存中的旧 SID。
        SynologySession session = login();
        sessionStore.put(sessionKey, SynologyCachedSession.of(session));
        return session;
    }

    public synchronized void clear() {
        sessionStore.remove(sessionKey);
    }

    public synchronized LogoutResponse logout() {
        SynologyCachedSession cachedSession = sessionStore.get(sessionKey);
        SynologySession session = cachedSession == null ? null : cachedSession.getSession();
        sessionStore.remove(sessionKey);
        return authClient.logout(session == null ? null : session.getSid());
    }

    private SynologySession login() {
        if (authClient == null) {
            throw new SynologyAuthException("authClient is not initialized");
        }
        return authClient.login();
    }
}
