package io.github.shuzhuoi.synology.example.common;

import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.filestation.download.DownloadFileResponse;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderRequest;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderResponse;
import io.github.shuzhuoi.synology.filestation.file.DeleteRequest;
import io.github.shuzhuoi.synology.filestation.file.DeleteResponse;
import io.github.shuzhuoi.synology.filestation.file.RenameRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesResponse;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileRequest;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileResponse;
import io.github.shuzhuoi.synology.model.Additional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通 Java、Spring Boot 2 和 Spring Boot 3 共用的基础 File Station 工作流。
 */
public class FileStationBasicWorkflow {

    /**
     * 执行目录查询、创建、上传、下载、重命名和删除，并在结束时尝试登出。
     */
    public FileStationBasicWorkflowResult execute(SynologyDsmClient client,
                                                  FileStationBasicWorkflowConfig config) throws IOException {
        if (client == null) {
            throw new IllegalArgumentException("client must not be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }
        // 登录成功后才进入 finally，避免登录失败时额外发送一次无 SID 的登出请求。
        client.session().currentSession();
        Throwable operationFailure = null;
        try {
            return executeOperations(client, config);
        } catch (IOException | RuntimeException exception) {
            operationFailure = exception;
            throw exception;
        } finally {
            // 即使中途调用失败也释放当前示例会话，避免示例运行后残留 SID。
            try {
                client.session().logout();
            } catch (RuntimeException logoutFailure) {
                if (operationFailure == null) {
                    throw logoutFailure;
                }
                operationFailure.addSuppressed(logoutFailure);
            }
        }
    }

    private FileStationBasicWorkflowResult executeOperations(SynologyDsmClient client,
                                                              FileStationBasicWorkflowConfig config) throws IOException {
        File sampleFile = config.getSampleFile();
        String remoteFolder = config.getRemoteFolder();
        File downloadFolder = config.getDownloadFolder();
        Files.createDirectories(downloadFolder.toPath());

        String uploadedPath = remoteFolder + "/" + sampleFile.getName();
        String renamedPath = remoteFolder + "/demo-renamed-" + sampleFile.getName();
        File downloadedFile = new File(downloadFolder, "downloaded-" + sampleFile.getName());

        client.fileStation().info().get();
        client.fileStation().list().shares();

        ListFilesResponse files = client.fileStation().list().files(
                ListFilesRequest.builder(parentPath(remoteFolder)).limit(20)
                        .addAdditional(Additional.SIZE).addAdditional(Additional.TIME).build());
        List<String> listedPaths = new ArrayList<>();
        for (SynologyFile file : files.getFiles()) {
            listedPaths.add(file.getPath());
        }

        CreateFolderResponse folder = client.fileStation().file().createFolder(
                CreateFolderRequest.builder().addFolder(parentPath(remoteFolder), folderName(remoteFolder))
                        .forceParent(Boolean.TRUE).build());
        String createdFolderPath = folder.getFolders().get(0).getPath();

        UploadFileResponse upload = client.fileStation().upload().file(
                UploadFileRequest.builder(remoteFolder, sampleFile).overwrite(Boolean.TRUE)
                        .createParents(Boolean.TRUE).build());

        saveDownloadedFile(client.fileStation().download().file(uploadedPath), downloadedFile);

        client.fileStation().file().rename(
                RenameRequest.builder().addRename(uploadedPath, fileName(renamedPath)).build());
        DeleteResponse delete = client.fileStation().file().delete(
                DeleteRequest.builder().addPath(renamedPath).build());

        return new FileStationBasicWorkflowResult(listedPaths, createdFolderPath, uploadedPath,
                upload.isSuccess(), downloadedFile.getAbsolutePath(), renamedPath, delete.isSuccess());
    }

    private void saveDownloadedFile(DownloadFileResponse response, File downloadedFile) throws IOException {
        InputStream inputStream = response.getInputStream();
        if (inputStream == null) {
            throw new IllegalStateException("下载响应中没有可读取的文件流。");
        }
        try (InputStream downloadInputStream = inputStream) {
            Files.copy(downloadInputStream, downloadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String parentPath(String path) {
        String normalized = trimTrailingSlash(path);
        int index = normalized.lastIndexOf('/');
        return index <= 0 ? "/" : normalized.substring(0, index);
    }

    private String folderName(String path) {
        String normalized = trimTrailingSlash(path);
        int index = normalized.lastIndexOf('/');
        return index < 0 || index == normalized.length() - 1 ? normalized : normalized.substring(index + 1);
    }

    private String fileName(String path) {
        return folderName(path);
    }

    private String trimTrailingSlash(String path) {
        String normalized = path;
        while (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
