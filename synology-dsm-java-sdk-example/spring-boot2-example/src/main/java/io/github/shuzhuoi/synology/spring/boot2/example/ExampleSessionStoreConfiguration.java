package io.github.shuzhuoi.synology.spring.boot2.example;

import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import io.github.shuzhuoi.synology.example.common.ExampleSessionStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 展示用户自定义 SessionStore Bean 后，Starter 会自动使用该实现。
 */
@Configuration
@ConditionalOnProperty(prefix = "example", name = "custom-session-store", havingValue = "true")
public class ExampleSessionStoreConfiguration {

    @Bean
    public SynologySessionStore exampleSessionStore() {
        return new ExampleSessionStore();
    }
}
