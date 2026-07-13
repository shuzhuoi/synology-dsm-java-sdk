package io.github.shuzhuoi.synology.spring.boot2;

import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyHttpClient;
import io.github.shuzhuoi.synology.http.okhttp3.OkHttp3SynologyHttpClient;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 2 自动装配。该类只负责把配置和扩展点组装成 core 客户端。
 */
@Configuration
@ConditionalOnClass(SynologyDsmClient.class)
@ConditionalOnProperty(prefix = "synology.dsm", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SynologyDsmProperties.class)
public class SynologyDsmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SynologyDsmConfig synologyDsmConfig(SynologyDsmProperties properties) {
        return properties.toConfig();
    }

    @Bean
    @ConditionalOnMissingBean(SynologyJsonCodec.class)
    @ConditionalOnClass(name = "io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec")
    public SynologyJsonCodec jacksonSynologyJsonCodec() {
        return new io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec();
    }

    @Bean
    @ConditionalOnMissingBean(SynologyHttpClient.class)
    @ConditionalOnProperty(prefix = "synology.dsm", name = "http-adapter", havingValue = "hutool", matchIfMissing = true)
    @ConditionalOnClass(HutoolSynologyHttpClient.class)
    public SynologyHttpClient hutoolSynologyHttpClient() {
        return new HutoolSynologyHttpClient();
    }

    @Bean
    @ConditionalOnMissingBean(SynologyHttpClient.class)
    @ConditionalOnProperty(prefix = "synology.dsm", name = "http-adapter", havingValue = "okhttp3")
    @ConditionalOnClass(name = "io.github.shuzhuoi.synology.http.okhttp3.OkHttp3SynologyHttpClient")
    public SynologyHttpClient okhttp3SynologyHttpClient() {
        return new OkHttp3SynologyHttpClient();
    }

    @Bean
    @ConditionalOnMissingBean(SynologyDsmClient.class)
    public SynologyDsmClient synologyDsmClient(
            SynologyDsmConfig config,
            ObjectProvider<SynologyHttpClient> httpClientProvider,
            ObjectProvider<SynologySessionStore> sessionStoreProvider,
            ObjectProvider<SynologyJsonCodec> jsonCodecProvider) {
        SynologyHttpClient httpClient = httpClientProvider.getIfAvailable();
        if (httpClient == null) {
            throw new IllegalStateException("No SynologyHttpClient is available for synology.dsm.http-adapter");
        }
        SynologyJsonCodec jsonCodec = jsonCodecProvider.getIfAvailable();
        if (jsonCodec == null) {
            throw new IllegalStateException("No SynologyJsonCodec is available; add a JSON implementation module");
        }
        return SynologyDsmClient.builder()
                .config(config)
                .httpClient(httpClient)
                .jsonCodec(jsonCodec)
                .sessionStore(sessionStoreProvider.getIfAvailable())
                .build();
    }
}
