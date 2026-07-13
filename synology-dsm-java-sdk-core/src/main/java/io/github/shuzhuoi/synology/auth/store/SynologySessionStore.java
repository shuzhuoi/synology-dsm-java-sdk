package io.github.shuzhuoi.synology.auth.store;

/**
 * DSM Session/SID 缓存扩展点。
 * <p>
 * core 只提供接口和默认本地 Map 实现。Redis、Caffeine、数据库、Spring Cache 等外部缓存
 * 由使用方在自己的项目中实现并通过 SynologyDsmClient.Builder 注入。
 */
public interface SynologySessionStore {

    /**
     * 读取缓存会话。不存在时返回 null。
     */
    SynologyCachedSession get(SynologySessionKey key);

    /**
     * 写入缓存会话。
     */
    void put(SynologySessionKey key, SynologyCachedSession session);

    /**
     * 移除指定缓存会话。
     */
    void remove(SynologySessionKey key);

    /**
     * 清空当前 store 中的所有缓存会话。
     */
    void clear();
}
