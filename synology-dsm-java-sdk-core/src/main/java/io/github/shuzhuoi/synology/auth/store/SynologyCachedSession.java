package io.github.shuzhuoi.synology.auth.store;

import io.github.shuzhuoi.synology.auth.SynologySession;

import java.util.Date;

/**
 * 缓存中的 DSM Session。
 * <p>
 * 不只保存 SID 字符串，是为了后续扩展创建时间、来源、过期判断等元数据时不破坏 store 接口。
 */
public class SynologyCachedSession {

    private final SynologySession session;
    private final Date cachedAt;

    public SynologyCachedSession(SynologySession session, Date cachedAt) {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        this.session = session;
        this.cachedAt = cachedAt == null ? new Date() : new Date(cachedAt.getTime());
    }

    public static SynologyCachedSession of(SynologySession session) {
        return new SynologyCachedSession(session, new Date());
    }

    public SynologySession getSession() {
        return session;
    }

    public Date getCachedAt() {
        return new Date(cachedAt.getTime());
    }
}
