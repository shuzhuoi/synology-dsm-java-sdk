package io.github.shuzhuoi.synology.spring.boot2.example;

import io.github.shuzhuoi.synology.auth.store.SynologyCachedSession;
import io.github.shuzhuoi.synology.auth.store.SynologySessionKey;
import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 示例用的自定义 SessionStore。
 *
 * <p>实际项目可将同样的接口实现替换为 Redis、数据库或其他缓存设施。</p>
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
