package io.github.shuzhuoi.synology.auth.store;

import io.github.shuzhuoi.synology.auth.SynologySession;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 覆盖默认本地 Map store，确认不同 key 的 SID 不会互相覆盖。
 */
class InMemorySynologySessionStoreTest {

    @Test
    void storesSessionsByCompositeKey() {
        InMemorySynologySessionStore store = new InMemorySynologySessionStore();
        SynologySessionKey firstKey = new SynologySessionKey("http://nas-a:5000", "alice", "FileStation");
        SynologySessionKey secondKey = new SynologySessionKey("http://nas-b:5000", "alice", "FileStation");

        store.put(firstKey, SynologyCachedSession.of(new SynologySession("sid-a", "FileStation", new Date())));
        store.put(secondKey, SynologyCachedSession.of(new SynologySession("sid-b", "FileStation", new Date())));

        assertEquals("sid-a", store.get(firstKey).getSession().getSid());
        assertEquals("sid-b", store.get(secondKey).getSession().getSid());
    }

    @Test
    void removeAndClearSessions() {
        InMemorySynologySessionStore store = new InMemorySynologySessionStore();
        SynologySessionKey firstKey = new SynologySessionKey("http://nas-a:5000", "alice", "FileStation");
        SynologySessionKey secondKey = new SynologySessionKey("http://nas-b:5000", "alice", "FileStation");

        store.put(firstKey, SynologyCachedSession.of(new SynologySession("sid-a", "FileStation", new Date())));
        store.put(secondKey, SynologyCachedSession.of(new SynologySession("sid-b", "FileStation", new Date())));
        store.remove(firstKey);

        assertNull(store.get(firstKey));
        assertEquals("sid-b", store.get(secondKey).getSession().getSid());

        store.clear();

        assertNull(store.get(secondKey));
    }
}
