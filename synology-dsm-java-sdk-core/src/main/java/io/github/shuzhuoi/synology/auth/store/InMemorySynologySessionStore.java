package io.github.shuzhuoi.synology.auth.store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 默认本地 Map SessionStore。
 * <p>
 * 该实现只在当前 JVM 内生效，适合普通单实例应用或示例场景。多实例共享 SID 时，
 * 请在业务项目中自行实现 SynologySessionStore 接入 Redis、Caffeine、数据库等设施。
 */
public class InMemorySynologySessionStore implements SynologySessionStore {

    private final ConcurrentMap<SynologySessionKey, SynologyCachedSession> sessions =
            new ConcurrentHashMap<SynologySessionKey, SynologyCachedSession>();

    @Override
    public SynologyCachedSession get(SynologySessionKey key) {
        if (key == null) {
            return null;
        }
        return sessions.get(key);
    }

    @Override
    public void put(SynologySessionKey key, SynologyCachedSession session) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        if (session == null) {
            sessions.remove(key);
            return;
        }
        sessions.put(key, session);
    }

    @Override
    public void remove(SynologySessionKey key) {
        if (key != null) {
            sessions.remove(key);
        }
    }

    @Override
    public void clear() {
        sessions.clear();
    }
}
