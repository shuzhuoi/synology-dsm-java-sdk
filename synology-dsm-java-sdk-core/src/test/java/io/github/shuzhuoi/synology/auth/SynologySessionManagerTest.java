package io.github.shuzhuoi.synology.auth;

import io.github.shuzhuoi.synology.auth.store.InMemorySynologySessionStore;
import io.github.shuzhuoi.synology.auth.store.SynologyCachedSession;
import io.github.shuzhuoi.synology.auth.store.SynologySessionKey;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.exception.SynologyAuthException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 覆盖会话管理器对 SynologySessionStore 的读写行为，保护懒登录和登出语义。
 */
class SynologySessionManagerTest {

    @Test
    void currentSessionLazyLoginAndCachesSessionInStore() {
        SynologyDsmConfig config = configBuilder().build();
        InMemorySynologySessionStore store = new InMemorySynologySessionStore();
        CountingAuthClient authClient = new CountingAuthClient(config, Arrays.asList("sid-1"));
        SynologySessionManager manager = new SynologySessionManager(config, authClient, store);

        assertEquals("sid-1", manager.currentSession().getSid());
        assertEquals("sid-1", manager.currentSession().getSid());
        assertEquals(1, authClient.getLoginCount());

        SynologyCachedSession cachedSession = store.get(SynologySessionKey.fromConfig(config));
        assertEquals("sid-1", cachedSession.getSession().getSid());
    }

    @Test
    void currentSessionUsesExistingStoreWithoutLogin() {
        SynologyDsmConfig config = configBuilder().build();
        InMemorySynologySessionStore store = new InMemorySynologySessionStore();
        store.put(
                SynologySessionKey.fromConfig(config),
                SynologyCachedSession.of(new SynologySession("cached-sid", config.getSessionName(), new Date()))
        );
        CountingAuthClient authClient = new CountingAuthClient(config, Arrays.asList("sid-1"));
        SynologySessionManager manager = new SynologySessionManager(config, authClient, store);

        assertEquals("cached-sid", manager.currentSession().getSid());
        assertEquals(0, authClient.getLoginCount());
    }

    @Test
    void currentSessionThrowsWhenAutoLoginDisabledAndNoCachedSession() {
        SynologyDsmConfig config = configBuilder().autoLogin(false).build();
        SynologySessionManager manager = new SynologySessionManager(
                config,
                new CountingAuthClient(config, Arrays.asList("sid-1")),
                new InMemorySynologySessionStore()
        );

        assertThrows(SynologyAuthException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                manager.currentSession();
            }
        });
    }

    @Test
    void refreshReplacesCachedSession() {
        SynologyDsmConfig config = configBuilder().build();
        InMemorySynologySessionStore store = new InMemorySynologySessionStore();
        CountingAuthClient authClient = new CountingAuthClient(config, Arrays.asList("sid-1", "sid-2"));
        SynologySessionManager manager = new SynologySessionManager(config, authClient, store);

        assertEquals("sid-1", manager.currentSession().getSid());
        assertEquals("sid-2", manager.refresh().getSid());
        assertEquals("sid-2", store.get(SynologySessionKey.fromConfig(config)).getSession().getSid());
    }

    @Test
    void logoutRemovesCachedSessionAndUsesCachedSid() {
        SynologyDsmConfig config = configBuilder().build();
        InMemorySynologySessionStore store = new InMemorySynologySessionStore();
        CountingAuthClient authClient = new CountingAuthClient(config, Arrays.asList("sid-1"));
        SynologySessionManager manager = new SynologySessionManager(config, authClient, store);

        manager.currentSession();
        manager.logout();

        assertEquals("sid-1", authClient.getLastLogoutSid());
        assertNull(store.get(SynologySessionKey.fromConfig(config)));
    }

    private SynologyDsmConfig.Builder configBuilder() {
        return SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .account("demo")
                .password("secret");
    }

    /**
     * 测试用认证客户端，只记录登录和登出调用，不访问真实 DSM。
     */
    private static class CountingAuthClient extends AuthClient {

        private final SynologyDsmConfig config;
        private final List<String> sids;
        private int loginCount;
        private String lastLogoutSid;

        CountingAuthClient(SynologyDsmConfig config, List<String> sids) {
            super(config, null);
            this.config = config;
            this.sids = sids;
        }

        @Override
        public SynologySession login() {
            String sid = sids.get(Math.min(loginCount, sids.size() - 1));
            loginCount++;
            return new SynologySession(sid, config.getSessionName(), new Date());
        }

        @Override
        public LogoutResponse logout(String sid) {
            this.lastLogoutSid = sid;
            return new LogoutResponse(true);
        }

        int getLoginCount() {
            return loginCount;
        }

        String getLastLogoutSid() {
            return lastLogoutSid;
        }
    }
}
