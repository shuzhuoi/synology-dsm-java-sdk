package io.github.shuzhuoi.synology.spring.boot3.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Spring Boot 3 Starter 使用入口。
 *
 * <p>启动后会根据 application.yml 自动创建 SynologyDsmClient。</p>
 */
@SpringBootApplication
@EnableConfigurationProperties(FileStationExampleProperties.class)
public class SpringBoot3StarterExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3StarterExampleApplication.class, args);
    }
}
