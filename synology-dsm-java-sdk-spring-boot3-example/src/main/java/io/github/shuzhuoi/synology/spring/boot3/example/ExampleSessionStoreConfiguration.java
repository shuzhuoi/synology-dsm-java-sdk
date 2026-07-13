package io.github.shuzhuoi.synology.spring.boot3.example;

import io.github.shuzhuoi.synology.auth.store.SynologySessionStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 展示用户自定义 SessionStore Bean 后，Starter 会自动使用该实现。
 */
@Configuration
public class ExampleSessionStoreConfiguration {

    @Bean
    public SynologySessionStore exampleSessionStore() {
        return new ExampleSessionStore();
    }
}
