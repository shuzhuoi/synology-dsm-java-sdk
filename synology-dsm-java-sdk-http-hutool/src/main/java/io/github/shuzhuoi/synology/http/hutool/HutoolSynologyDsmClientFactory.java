package io.github.shuzhuoi.synology.http.hutool;

import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;

public final class HutoolSynologyDsmClientFactory {

    private HutoolSynologyDsmClientFactory() {
    }

    public static SynologyDsmClient create(SynologyDsmConfig config, SynologyJsonCodec jsonCodec) {
        return SynologyDsmClient.builder()
                .config(config)
                .httpClient(new HutoolSynologyHttpClient())
                .jsonCodec(jsonCodec)
                .build();
    }

    public static SynologyDsmClient create(
            SynologyDsmConfig config,
            SynologyJsonCodec jsonCodec,
            SynologySessionStore sessionStore) {
        return SynologyDsmClient.builder()
                .config(config)
                .httpClient(new HutoolSynologyHttpClient())
                .jsonCodec(jsonCodec)
                .sessionStore(sessionStore)
                .build();
    }
}
