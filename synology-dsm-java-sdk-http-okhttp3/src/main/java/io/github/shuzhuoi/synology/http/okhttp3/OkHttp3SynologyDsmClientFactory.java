package io.github.shuzhuoi.synology.http.okhttp3;

import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;

/**
 * 使用 OkHttp3 adapter 创建 SynologyDsmClient 的便捷工厂。
 */
public final class OkHttp3SynologyDsmClientFactory {

    private OkHttp3SynologyDsmClientFactory() {
    }

    /**
     * 使用默认 OkHttpClient 和默认本地 SessionStore 创建客户端。
     */
    public static SynologyDsmClient create(SynologyDsmConfig config, SynologyJsonCodec jsonCodec) {
        return SynologyDsmClient.builder()
                .config(config)
                .httpClient(new OkHttp3SynologyHttpClient())
                .jsonCodec(jsonCodec)
                .build();
    }

    /**
     * 使用默认 OkHttpClient 和指定 SessionStore 创建客户端。
     */
    public static SynologyDsmClient create(
            SynologyDsmConfig config,
            SynologyJsonCodec jsonCodec,
            SynologySessionStore sessionStore) {
        return SynologyDsmClient.builder()
                .config(config)
                .httpClient(new OkHttp3SynologyHttpClient())
                .jsonCodec(jsonCodec)
                .sessionStore(sessionStore)
                .build();
    }
}
