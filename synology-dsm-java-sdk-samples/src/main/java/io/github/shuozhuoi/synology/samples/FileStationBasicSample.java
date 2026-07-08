package io.github.shuzhuoi.synology.samples;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.filestation.download.DownloadFileResponse;
import io.github.shuzhuoi.synology.filestation.file.*;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesResponse;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileRequest;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileResponse;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.model.Additional;
import io.github.shuzhuoi.synology.samples.config.FileStationBasicSampleConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 基础示例。
 * <p>
 * 运行前请复制 classpath 下的 filestation-basic.example.yaml 为 filestation-basic.yaml，
 * 并在 filestation-basic.yaml 中填写真实 DSM 地址、账号、密码、本地示例文件、远端示例目录和本地下载目录。
 */
@Slf4j
public class FileStationBasicSample {

    private static final String CONFIG_FILE = "filestation-basic.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "filestation-basic.example.yaml";

    public static void main(String[] args) throws IOException {
        FileStationBasicSampleConfig sampleConfig = readSampleConfig();
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(requiredConfigValue(sampleConfig.getDsmUrl(), "dsmUrl"))
                .account(requiredConfigValue(sampleConfig.getAccount(), "account"))
                .password(requiredConfigValue(sampleConfig.getPassword(), "password"))
                .build();

        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(config);
        File sampleFile = new File(requiredConfigValue(sampleConfig.getSampleFile(), "sampleFile"));
        String remoteFolder = requiredConfigValue(sampleConfig.getSampleFolder(), "sampleFolder");
        File downloadFolder = FileUtil.mkdir(requiredConfigValue(sampleConfig.getDownloadFolder(), "downloadFolder"));
        String uploadedPath = remoteFolder + "/" + sampleFile.getName();
        String renamedPath = remoteFolder + "/demo-renamed-" + sampleFile.getName();
        File downloadedFile = FileUtil.file(downloadFolder, "downloaded-" + sampleFile.getName());

        client.session().currentSession();
        client.fileStation().info().get();
        client.fileStation().list().shares();

        ListFilesResponse files = client.fileStation().list().files(
                ListFilesRequest.builder(parentPath(remoteFolder))
                        .limit(20)
                        .addAdditional(Additional.SIZE)
                        .addAdditional(Additional.TIME)
                        .build()
        );
        log.info("目录文件列表：");
        for (SynologyFile file : files.getFiles()) {
            log.info("{}", file.getPath());
        }

        CreateFolderResponse folder = client.fileStation().file().createFolder(
                CreateFolderRequest.builder()
                        .addFolder(parentPath(remoteFolder), folderName(remoteFolder))
                        .forceParent(Boolean.TRUE)
                        .build()
        );
        log.info("创建目录：{}", folder.getFolders().get(0).getPath());

        UploadFileResponse uploadResponse = client.fileStation().upload().file(
                UploadFileRequest.builder(remoteFolder, sampleFile)
                        .overwrite(Boolean.TRUE)
                        .createParents(Boolean.TRUE)
                        .build()
        );
        log.info("上传文件：{}，成功：{}", uploadedPath, uploadResponse.isSuccess());

        DownloadFileResponse downloadResponse = client.fileStation().download().file(uploadedPath);
        saveDownloadedFile(downloadResponse, downloadedFile);
        log.info("下载文件：{}", downloadedFile.getAbsolutePath());

        client.fileStation().file().rename(
                RenameRequest.builder()
                        .addRename(uploadedPath, fileName(renamedPath))
                        .build()
        );
        log.info("重命名文件：{} -> {}", uploadedPath, renamedPath);

        DeleteResponse delete = client.fileStation().file().delete(
                DeleteRequest.builder()
                        .addPath(renamedPath)
                        .build()
        );
        log.info("删除文件：{}，成功：{}", renamedPath, delete.isSuccess());

        client.session().logout();
    }

    private static FileStationBasicSampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = FileStationBasicSample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, FileStationBasicSampleConfig.class);
        }
    }

    private static String requiredConfigValue(String value, String fieldName) {
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException("示例配置文件 " + CONFIG_FILE + " 缺少必填配置：" + fieldName);
        }
        return value;
    }

    private static void saveDownloadedFile(DownloadFileResponse downloadResponse, File downloadedFile) throws IOException {
        InputStream inputStream = downloadResponse.getInputStream();
        if (inputStream == null) {
            throw new IllegalStateException("下载响应中没有可读取的文件流。");
        }
        try (InputStream downloadInputStream = inputStream) {
            // 使用 Hutool 写入下载流，示例运行后可以直接在 downloadFolder 中看到下载文件。
            FileUtil.writeFromStream(downloadInputStream, downloadedFile);
        }
    }

    private static String parentPath(String path) {
        String normalized = trimTrailingSlash(path);
        int index = normalized.lastIndexOf('/');
        if (index <= 0) {
            return "/";
        }
        return normalized.substring(0, index);
    }

    private static String folderName(String path) {
        String normalized = trimTrailingSlash(path);
        int index = normalized.lastIndexOf('/');
        if (index < 0 || index == normalized.length() - 1) {
            return normalized;
        }
        return normalized.substring(index + 1);
    }

    private static String fileName(String path) {
        return folderName(path);
    }

    private static String trimTrailingSlash(String path) {
        String normalized = path;
        while (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
