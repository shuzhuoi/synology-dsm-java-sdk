package io.github.shuzhuoi.synology.spring.boot2.example;

import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 用户自定义 JSON Codec Bean 示例。
 * <p>
 * 默认不启用；激活 custom-json-codec profile 后，Starter 会使用该 Bean，
 * 不再创建默认 Jackson Codec。
 */
@Configuration
@Profile("custom-json-codec")
public class CustomJsonCodecConfiguration {

    @Bean
    public SynologyJsonCodec synologyJsonCodec() {
        return new JacksonSynologyJsonCodec();
    }
}
