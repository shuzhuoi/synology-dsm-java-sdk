package io.github.shuzhuoi.synology.example;

import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflow;
import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflowConfig;
import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflowReporter;
import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflowResult;
import io.github.shuzhuoi.synology.example.config.FileStationBasicExampleConfig;
import io.github.shuzhuoi.synology.example.support.ExampleConfigReader;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 基础示例。
 * <p>
 * 运行前请复制 classpath 下的 filestation-basic.example.yaml 为 filestation-basic.yaml，
 * 并在 filestation-basic.yaml 中填写真实 DSM 地址、账号、密码、本地示例文件、远端示例目录和本地下载目录。
 */
@Slf4j
public class FileStationBasicExample {

    private static final String CONFIG_FILE = "filestation-basic.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "filestation-basic.example.yaml";

    public static void main(String[] args) throws IOException {
        FileStationBasicExampleConfig sampleConfig = ExampleConfigReader.readYaml(
                FileStationBasicExample.class,
                CONFIG_FILE,
                CONFIG_EXAMPLE_FILE,
                FileStationBasicExampleConfig.class
        );
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(ExampleConfigReader.requiredValue(sampleConfig.getDsmUrl(), CONFIG_FILE, "dsmUrl"))
                .account(ExampleConfigReader.requiredValue(sampleConfig.getAccount(), CONFIG_FILE, "account"))
                .password(ExampleConfigReader.requiredValue(sampleConfig.getPassword(), CONFIG_FILE, "password"))
                .build();

        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(
                config,
                new JacksonSynologyJsonCodec()
        );
        FileStationBasicWorkflowConfig workflowConfig = new FileStationBasicWorkflowConfig(
                new File(ExampleConfigReader.requiredValue(sampleConfig.getSampleFile(), CONFIG_FILE, "sampleFile")),
                ExampleConfigReader.requiredValue(sampleConfig.getSampleFolder(), CONFIG_FILE, "sampleFolder"),
                new File(ExampleConfigReader.requiredValue(sampleConfig.getDownloadFolder(), CONFIG_FILE, "downloadFolder"))
        );
        FileStationBasicWorkflowResult result = new FileStationBasicWorkflow().execute(client, workflowConfig);
        FileStationBasicWorkflowReporter.report(result, log::info);
    }
}
