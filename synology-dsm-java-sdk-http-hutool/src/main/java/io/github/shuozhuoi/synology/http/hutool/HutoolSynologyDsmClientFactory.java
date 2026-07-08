package io.github.shuzhuoi.synology.http.hutool;

import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;

public final class HutoolSynologyDsmClientFactory {

    private HutoolSynologyDsmClientFactory() {
    }

    public static SynologyDsmClient create(SynologyDsmConfig config) {
        return SynologyDsmClient.builder()
                .config(config)
                .httpClient(new HutoolSynologyHttpClient())
                .build();
    }
}
