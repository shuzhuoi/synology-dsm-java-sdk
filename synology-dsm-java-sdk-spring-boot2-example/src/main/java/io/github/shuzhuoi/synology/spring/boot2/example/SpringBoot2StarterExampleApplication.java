package io.github.shuzhuoi.synology.spring.boot2.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 2 Starter 使用入口。
 *
 * <p>启动后会根据 application.yml 自动创建 SynologyDsmClient。</p>
 */
@SpringBootApplication
public class SpringBoot2StarterExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot2StarterExampleApplication.class, args);
    }
}
