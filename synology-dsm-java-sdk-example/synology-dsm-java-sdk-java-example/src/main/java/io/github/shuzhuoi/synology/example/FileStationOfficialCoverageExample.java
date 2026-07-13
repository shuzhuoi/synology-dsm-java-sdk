package io.github.shuzhuoi.synology.example;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.example.config.FileStationOfficialCoverageExampleConfig;
import io.github.shuzhuoi.synology.filestation.file.CopyMoveRequest;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderRequest;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderResponse;
import io.github.shuzhuoi.synology.filestation.file.DeleteRequest;
import io.github.shuzhuoi.synology.filestation.file.RenameRequest;
import io.github.shuzhuoi.synology.filestation.permission.CheckPermissionWriteRequest;
import io.github.shuzhuoi.synology.filestation.permission.CheckPermissionWriteResponse;
import io.github.shuzhuoi.synology.filestation.search.SearchStartRequest;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.task.TaskStatusResponse;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileRequest;
import io.github.shuzhuoi.synology.filestation.upload.UploadOverwritePolicy;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import io.github.shuzhuoi.synology.model.Additional;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * File Station 官方契约补齐示例。
 * <p>
 * 专门演示本次依据官方文档补齐的能力：
 * CheckPermission.write、Upload 时间戳与 v3 覆盖策略、CreateFolder/Rename additional、
 * Rename/CopyMove/Delete search_task_id、Delete.delete 阻塞删除、Search 多 taskid stop/clean。
 * <p>
 * 运行前请复制 classpath 下的 filestation-official-coverage.example.yaml 为
 * filestation-official-coverage.yaml，并填写真实 DSM 地址、账号、密码和远端测试目录。
 * 示例只会删除自己创建的 run-* 子目录，不会直接删除 sampleFolder。
 */
@Slf4j
public class FileStationOfficialCoverageExample {

    private static final String CONFIG_FILE = "filestation-official-coverage.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "filestation-official-coverage.example.yaml";
    private static final String RUN_FOLDER_PREFIX = "run-";
    private static final String RENAMED_PREFIX = "sdk-official-renamed-";

    public static void main(String[] args) throws IOException {
        FileStationOfficialCoverageExampleConfig sampleConfig = readSampleConfig();
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(requiredConfigValue(sampleConfig.getDsmUrl(), "dsmUrl"))
                .account(requiredConfigValue(sampleConfig.getAccount(), "account"))
                .password(requiredConfigValue(sampleConfig.getPassword(), "password"))
                .autoRefreshSession(Boolean.TRUE)
                .build();

        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(config, new JacksonSynologyJsonCodec());
        String sampleFolder = trimTrailingSlash(requiredConfigValue(sampleConfig.getSampleFolder(), "sampleFolder"));
        String runFolder = sampleFolder + "/" + RUN_FOLDER_PREFIX + System.currentTimeMillis();
        String searchTaskId = null;
        File localFile = null;

        try {
            client.session().currentSession();
            createRunFolder(client, runFolder);

            localFile = createLocalSampleFile();
            String remoteFilename = localFile.getName();
            String uploadedPath = runFolder + "/" + remoteFilename;
            String renamedFilename = RENAMED_PREFIX + remoteFilename;
            String renamedPath = runFolder + "/" + renamedFilename;
            String copyFolder = runFolder + "/copies";

            checkWritePermission(client, runFolder, remoteFilename);
            uploadWithOfficialOptions(client, runFolder, localFile);

            searchTaskId = startSearchForUploadedFile(client, runFolder, remoteFilename);
            renameWithAdditionalAndSearchTask(client, uploadedPath, renamedFilename, searchTaskId);

            createCopyFolder(client, copyFolder);
            copyWithSearchTask(client, renamedPath, copyFolder, searchTaskId);
            deleteBlockingWithSearchTask(client, copyFolder, searchTaskId);

            multiSearchStopCleanDemo(client, runFolder);
            cleanSearchQuietly(client, searchTaskId);
            searchTaskId = null;

            log.info("官方契约补齐示例执行完成，准备清理测试目录：{}", runFolder);
        } finally {
            if (localFile != null) {
                FileUtil.del(localFile);
            }
            cleanSearchQuietly(client, searchTaskId);
            cleanupRunFolder(client, runFolder);
            client.session().logout();
        }
    }

    private static void createRunFolder(SynologyDsmClient client, String runFolder) {
        CreateFolderResponse response = client.fileStation().file().createFolder(
                CreateFolderRequest.builder()
                        .addFolder(parentPath(runFolder), folderName(runFolder))
                        .forceParent(Boolean.TRUE)
                        .addAdditional(Additional.TIME)
                        .build()
        );
        log.info("创建本次运行目录：{}，返回目录数={}", runFolder, response.getFolders().size());
    }

    private static File createLocalSampleFile() throws IOException {
        File localFile = File.createTempFile("sdk-official-source-", ".txt");
        FileUtil.writeUtf8String("synology-dsm-java-sdk official coverage example", localFile);
        return localFile;
    }

    private static void checkWritePermission(SynologyDsmClient client, String runFolder, String remoteFilename) {
        CheckPermissionWriteResponse response = client.fileStation().permission().write(
                CheckPermissionWriteRequest.builder(runFolder, remoteFilename)
                        .overwrite(Boolean.TRUE)
                        .createOnly(Boolean.TRUE)
                        .build()
        );
        log.info("写入权限检查通过：folder={}，filename={}，allowed={}", runFolder, remoteFilename, response.isAllowed());
    }

    private static void uploadWithOfficialOptions(SynologyDsmClient client, String runFolder, File localFile) {
        long now = System.currentTimeMillis();
        client.fileStation().upload().file(
                UploadFileRequest.builder(runFolder, localFile)
                        .createParents(Boolean.TRUE)
                        .overwritePolicy(UploadOverwritePolicy.OVERWRITE)
                        .mtime(now)
                        .crtime(now)
                        .atime(now)
                        .build()
        );
        log.info("上传文件并设置时间戳与 v3 覆盖策略：{}/{}", runFolder, localFile.getName());
    }

    private static String startSearchForUploadedFile(SynologyDsmClient client, String runFolder, String remoteFilename) {
        TaskStartResponse response = client.fileStation().search().start(
                SearchStartRequest.builder(runFolder)
                        .recursive(Boolean.TRUE)
                        .pattern(remoteFilename)
                        .filetype("file")
                        .build()
        );
        log.info("启动搜索任务用于后续 search_task_id 联动：taskid={}", response.getTaskid());
        return response.getTaskid();
    }

    private static void renameWithAdditionalAndSearchTask(SynologyDsmClient client, String uploadedPath, String renamedFilename, String searchTaskId) {
        client.fileStation().file().rename(
                RenameRequest.builder()
                        .addRename(uploadedPath, renamedFilename)
                        .addAdditional(Additional.SIZE)
                        .addAdditional(Additional.TIME)
                        .searchTaskId(searchTaskId)
                        .build()
        );
        log.info("重命名并携带 additional/search_task_id：{} -> {}", uploadedPath, renamedFilename);
    }

    private static void createCopyFolder(SynologyDsmClient client, String copyFolder) {
        client.fileStation().file().createFolder(
                CreateFolderRequest.builder()
                        .addFolder(parentPath(copyFolder), folderName(copyFolder))
                        .forceParent(Boolean.TRUE)
                        .addAdditional(Additional.TIME)
                        .build()
        );
        log.info("创建复制目标目录：{}", copyFolder);
    }

    private static void copyWithSearchTask(SynologyDsmClient client, String renamedPath, String copyFolder, String searchTaskId) {
        TaskStartResponse response = client.fileStation().file().copy(
                CopyMoveRequest.builder(copyFolder)
                        .addPath(renamedPath)
                        .overwrite(Boolean.TRUE)
                        .accurateProgress(Boolean.TRUE)
                        .searchTaskId(searchTaskId)
                        .build()
        );
        waitCopyMoveFinished(client, response.getTaskid());
        log.info("复制并携带 search_task_id：{} -> {}，taskid={}", renamedPath, copyFolder, response.getTaskid());
    }

    private static void deleteBlockingWithSearchTask(SynologyDsmClient client, String copyFolder, String searchTaskId) {
        client.fileStation().file().deleteBlocking(
                DeleteRequest.builder()
                        .addPath(copyFolder)
                        .recursive(Boolean.TRUE)
                        .searchTaskId(searchTaskId)
                        .build()
        );
        log.info("官方阻塞 delete 已删除复制目录：{}", copyFolder);
    }

    private static void multiSearchStopCleanDemo(SynologyDsmClient client, String runFolder) {
        List<String> taskIds = new ArrayList<String>();
        TaskStartResponse first = client.fileStation().search().start(
                SearchStartRequest.builder(runFolder)
                        .recursive(Boolean.TRUE)
                        .pattern("*.txt")
                        .filetype("file")
                        .build()
        );
        taskIds.add(first.getTaskid());

        TaskStartResponse second = client.fileStation().search().start(
                SearchStartRequest.builder(runFolder)
                        .recursive(Boolean.TRUE)
                        .pattern(RENAMED_PREFIX + "*")
                        .filetype("file")
                        .build()
        );
        taskIds.add(second.getTaskid());

        client.fileStation().search().stop(taskIds);
        client.fileStation().search().clean(taskIds);
        log.info("Search 多 taskid stop/clean 已执行：{}", taskIds);
    }

    private static void waitCopyMoveFinished(SynologyDsmClient client, String taskId) {
        for (int i = 0; i < 120; i++) {
            TaskStatusResponse status = client.fileStation().file().copyMoveStatus(taskId);
            if (Boolean.TRUE.equals(status.getFinished())) {
                return;
            }
            sleepQuiet(500L);
        }
        throw new IllegalStateException("CopyMove task did not finish in time: " + taskId);
    }

    private static void cleanupRunFolder(SynologyDsmClient client, String runFolder) {
        try {
            client.fileStation().file().delete(
                    DeleteRequest.builder()
                            .addPath(runFolder)
                            .recursive(Boolean.TRUE)
                            .build()
            );
            log.info("已清理本次运行目录：{}", runFolder);
        } catch (Exception e) {
            log.warn("清理本次运行目录失败：{}，请手动检查。原因：{}", runFolder, e.getMessage());
        }
    }

    private static void cleanSearchQuietly(SynologyDsmClient client, String taskId) {
        if (taskId == null || taskId.trim().length() == 0) {
            return;
        }
        try {
            client.fileStation().search().clean(taskId);
        } catch (Exception e) {
            log.debug("搜索任务清理失败或已清理：taskid={}，原因={}", taskId, e.getMessage());
        }
    }

    private static FileStationOfficialCoverageExampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = FileStationOfficialCoverageExample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, FileStationOfficialCoverageExampleConfig.class);
        }
    }

    private static String requiredConfigValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("示例配置文件 " + CONFIG_FILE + " 缺少必填配置：" + fieldName);
        }
        return value;
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

    private static String trimTrailingSlash(String path) {
        String normalized = path;
        while (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static void sleepQuiet(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
