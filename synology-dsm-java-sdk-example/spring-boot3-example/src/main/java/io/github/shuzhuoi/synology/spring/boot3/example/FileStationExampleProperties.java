package io.github.shuzhuoi.synology.spring.boot3.example;

import io.github.shuzhuoi.synology.example.common.FileStationBasicWorkflowConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * Boot 3 基础示例使用的本地文件和远端目录配置。
 */
@ConfigurationProperties(prefix = "example.file-station")
public class FileStationExampleProperties {

    private String sampleFile;
    private String remoteFolder;
    private String downloadFolder;

    public FileStationBasicWorkflowConfig toWorkflowConfig() {
        return new FileStationBasicWorkflowConfig(new File(required(sampleFile, "sample-file")),
                required(remoteFolder, "remote-folder"), new File(required(downloadFolder, "download-folder")));
    }

    private String required(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("example.file-station." + name + " must not be blank");
        }
        return value;
    }

    public String getSampleFile() { return sampleFile; }
    public void setSampleFile(String value) { this.sampleFile = value; }
    public String getRemoteFolder() { return remoteFolder; }
    public void setRemoteFolder(String value) { this.remoteFolder = value; }
    public String getDownloadFolder() { return downloadFolder; }
    public void setDownloadFolder(String value) { this.downloadFolder = value; }
}
