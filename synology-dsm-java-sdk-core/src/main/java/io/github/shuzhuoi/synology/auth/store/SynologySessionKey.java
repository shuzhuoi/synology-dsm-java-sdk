package io.github.shuzhuoi.synology.auth.store;

import io.github.shuzhuoi.synology.config.SynologyDsmConfig;

import java.util.Objects;

/**
 * Session/SID 缓存键。
 * <p>
 * 同一个应用可能同时连接多个 DSM、多个账号或多个 WebAPI session，
 * 因此 key 必须包含 baseUrl、account、sessionName，避免 SID 被错误复用。
 */
public final class SynologySessionKey {

    private final String baseUrl;
    private final String account;
    private final String sessionName;

    public SynologySessionKey(String baseUrl, String account, String sessionName) {
        this.baseUrl = baseUrl;
        this.account = account;
        this.sessionName = sessionName;
    }

    public static SynologySessionKey fromConfig(SynologyDsmConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }
        return new SynologySessionKey(config.getBaseUrl(), config.getAccount(), config.getSessionName());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAccount() {
        return account;
    }

    public String getSessionName() {
        return sessionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SynologySessionKey)) {
            return false;
        }
        SynologySessionKey that = (SynologySessionKey) o;
        return Objects.equals(baseUrl, that.baseUrl)
                && Objects.equals(account, that.account)
                && Objects.equals(sessionName, that.sessionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, account, sessionName);
    }
}
