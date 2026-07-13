package io.github.shuzhuoi.synology.example.common;

import io.github.shuzhuoi.synology.auth.store.SynologyCachedSession;
import io.github.shuzhuoi.synology.auth.store.SynologySessionKey;
import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring Boot 示例共用的自定义 SessionStore。
 *
 * <p>实际项目可以用相同接口接入 Redis、数据库或其他缓存设施。</p>
 */
public class ExampleSessionStore implements SynologySessionStore {

    private final Map<SynologySessionKey, SynologyCachedSession> sessions = new ConcurrentHashMap<>();

    @Override
    public SynologyCachedSession get(SynologySessionKey key) {
        return sessions.get(key);
    }

    @Override
    public void put(SynologySessionKey key, SynologyCachedSession session) {
        sessions.put(key, session);
    }

    @Override
    public void remove(SynologySessionKey key) {
        sessions.remove(key);
    }

    @Override
    public void clear() {
        sessions.clear();
    }
}
