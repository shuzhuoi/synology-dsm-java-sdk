package io.github.shuzhuoi.synology.example.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基础 File Station 示例执行结果，供普通 Java 和 Spring Boot 示例统一输出。
 */
public class FileStationBasicWorkflowResult {

    private final List<String> listedPaths;
    private final String createdFolderPath;
    private final String uploadedPath;
    private final boolean uploadSuccess;
    private final String downloadedFilePath;
    private final String renamedPath;
    private final boolean deleteSuccess;

    public FileStationBasicWorkflowResult(List<String> listedPaths, String createdFolderPath,
                                          String uploadedPath, boolean uploadSuccess,
                                          String downloadedFilePath, String renamedPath,
                                          boolean deleteSuccess) {
        this.listedPaths = Collections.unmodifiableList(new ArrayList<>(listedPaths));
        this.createdFolderPath = createdFolderPath;
        this.uploadedPath = uploadedPath;
        this.uploadSuccess = uploadSuccess;
        this.downloadedFilePath = downloadedFilePath;
        this.renamedPath = renamedPath;
        this.deleteSuccess = deleteSuccess;
    }

    public List<String> getListedPaths() { return listedPaths; }
    public String getCreatedFolderPath() { return createdFolderPath; }
    public String getUploadedPath() { return uploadedPath; }
    public boolean isUploadSuccess() { return uploadSuccess; }
    public String getDownloadedFilePath() { return downloadedFilePath; }
    public String getRenamedPath() { return renamedPath; }
    public boolean isDeleteSuccess() { return deleteSuccess; }
}
