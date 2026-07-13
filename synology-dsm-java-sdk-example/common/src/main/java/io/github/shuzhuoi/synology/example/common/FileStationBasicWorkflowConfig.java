package io.github.shuzhuoi.synology.example.common;

import java.io.File;

/**
 * 基础 File Station 示例工作流配置。
 */
public class FileStationBasicWorkflowConfig {

    private final File sampleFile;
    private final String remoteFolder;
    private final File downloadFolder;

    public FileStationBasicWorkflowConfig(File sampleFile, String remoteFolder, File downloadFolder) {
        if (sampleFile == null) {
            throw new IllegalArgumentException("sampleFile must not be null");
        }
        if (remoteFolder == null || remoteFolder.trim().isEmpty()) {
            throw new IllegalArgumentException("remoteFolder must not be blank");
        }
        if (downloadFolder == null) {
            throw new IllegalArgumentException("downloadFolder must not be null");
        }
        this.sampleFile = sampleFile;
        this.remoteFolder = remoteFolder;
        this.downloadFolder = downloadFolder;
    }

    public File getSampleFile() {
        return sampleFile;
    }

    public String getRemoteFolder() {
        return remoteFolder;
    }

    public File getDownloadFolder() {
        return downloadFolder;
    }
}
