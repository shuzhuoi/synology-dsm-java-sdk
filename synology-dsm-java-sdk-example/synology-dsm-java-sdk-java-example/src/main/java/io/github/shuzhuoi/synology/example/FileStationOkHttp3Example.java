package io.github.shuzhuoi.synology.example;

import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.example.config.FileStationBasicExampleConfig;
import io.github.shuzhuoi.synology.example.support.ExampleConfigReader;
import io.github.shuzhuoi.synology.http.okhttp3.OkHttp3SynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 使用 OkHttp3 adapter 调用 File Station 的基础示例。
 * <p>
 * 本示例与 Hutool 基础示例复用同一份配置，说明两种 adapter 不改变 core 的公开调用方式。
 */
@Slf4j
public class FileStationOkHttp3Example {

    private static final String CONFIG_FILE = "filestation-basic.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "filestation-basic.example.yaml";

    public static void main(String[] args) throws IOException {
        FileStationBasicExampleConfig sampleConfig = ExampleConfigReader.readYaml(
                FileStationOkHttp3Example.class,
                CONFIG_FILE,
                CONFIG_EXAMPLE_FILE,
                FileStationBasicExampleConfig.class
        );
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(ExampleConfigReader.requiredValue(sampleConfig.getDsmUrl(), CONFIG_FILE, "dsmUrl"))
                .account(ExampleConfigReader.requiredValue(sampleConfig.getAccount(), CONFIG_FILE, "account"))
                .password(ExampleConfigReader.requiredValue(sampleConfig.getPassword(), CONFIG_FILE, "password"))
                .build();

        SynologyDsmClient client = OkHttp3SynologyDsmClientFactory.create(
                config,
                new JacksonSynologyJsonCodec()
        );
        try {
            client.session().currentSession();
            client.fileStation().info().get();
            client.fileStation().list().shares();
            log.info("OkHttp3 adapter File Station 基础调用完成。");
        } finally {
            client.session().logout();
        }
    }
}
