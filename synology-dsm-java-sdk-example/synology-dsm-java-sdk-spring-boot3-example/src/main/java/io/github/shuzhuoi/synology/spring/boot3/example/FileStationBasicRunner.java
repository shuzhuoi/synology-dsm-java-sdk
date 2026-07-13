package io.github.shuzhuoi.synology.spring.boot3.example;

import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflow;
import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflowReporter;
import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflowResult;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * 通过构造器注入 Starter 自动创建的客户端，并执行完整基础调用链。
 */
@Component
public class FileStationBasicRunner implements ApplicationRunner {

    private static final Logger LOGGER = Logger.getLogger(FileStationBasicRunner.class.getName());
    private final SynologyDsmClient client;
    private final FileStationExampleProperties properties;

    public FileStationBasicRunner(SynologyDsmClient client, FileStationExampleProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileStationBasicWorkflowResult result = new FileStationBasicWorkflow()
                .execute(client, properties.toWorkflowConfig());
        FileStationBasicWorkflowReporter.report(result, LOGGER::info);
    }
}
