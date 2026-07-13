package io.github.shuzhuoi.synology.example;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.example.config.FileStationArchiveExampleConfig;
import io.github.shuzhuoi.synology.filestation.compress.CompressStartRequest;
import io.github.shuzhuoi.synology.filestation.compress.CompressFormat;
import io.github.shuzhuoi.synology.filestation.compress.CompressLevel;
import io.github.shuzhuoi.synology.filestation.compress.CompressMode;
import io.github.shuzhuoi.synology.filestation.compress.CompressStatusResponse;
import io.github.shuzhuoi.synology.filestation.extract.ArchiveItem;
import io.github.shuzhuoi.synology.filestation.extract.ExtractListRequest;
import io.github.shuzhuoi.synology.filestation.extract.ExtractListResponse;
import io.github.shuzhuoi.synology.filestation.extract.ExtractStartRequest;
import io.github.shuzhuoi.synology.filestation.extract.ExtractStatusResponse;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderRequest;
import io.github.shuzhuoi.synology.filestation.file.DeleteRequest;
import io.github.shuzhuoi.synology.filestation.task.TaskPollingOptions;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileRequest;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileResponse;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * File Station 归档能力示例。
 * <p>
 * 演示 Compress 的 start / wait，以及 Extract 的 list / start / wait。
 * 运行前请复制 classpath 下的 filestation-archive.example.yaml 为 filestation-archive.yaml，
 * 并填写真实 DSM 地址、账号、密码和远端测试根目录。
 * <p>
 * 示例每次运行都会创建唯一远端子目录，结束时只清理本次运行产生的数据。
 */
@Slf4j
public class FileStationArchiveExample {

    private static final String CONFIG_FILE = "filestation-archive.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "filestation-archive.example.yaml";
    private static final String ARCHIVE_FILE_NAME = "sdk-archive-example.zip";

    /**
     * 轮询配置：500ms 间隔、最多 240 次，覆盖中小文件压缩和解压的典型耗时。
     */
    private static final TaskPollingOptions POLLING_OPTIONS = TaskPollingOptions.builder()
            .intervalMillis(500L)
            .maxAttempts(240)
            .build();

    public static void main(String[] args) throws IOException {
        FileStationArchiveExampleConfig sampleConfig = readSampleConfig();
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(requiredConfigValue(sampleConfig.getDsmUrl(), "dsmUrl"))
                .account(requiredConfigValue(sampleConfig.getAccount(), "account"))
                .password(requiredConfigValue(sampleConfig.getPassword(), "password"))
                .build();

        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(config, new JacksonSynologyJsonCodec());
        String sampleFolder = requiredConfigValue(sampleConfig.getSampleFolder(), "sampleFolder");
        String runFolder = appendPath(sampleFolder, "sdk-archive-" + System.currentTimeMillis());

        try {
            // 步骤 1：创建本次运行的远端目录并上传两个临时文本文件。
            List<String> sourcePaths = prepareTestData(client, runFolder);

            // 步骤 2：压缩测试文件并等待异步任务完成。
            String archivePath = appendPath(runFolder, ARCHIVE_FILE_NAME);
            compressDemo(client, sourcePaths, archivePath);

            // 步骤 3：读取压缩包目录，展示可用于部分解压的 itemId。
            listArchiveDemo(client, archivePath);

            // 步骤 4：解压全部文件并等待异步任务完成。
            extractDemo(client, archivePath, appendPath(runFolder, "extracted"));
        } finally {
            // 步骤 5：只清理本次运行的唯一子目录，不影响 sampleFolder 中的其他数据。
            cleanupTestData(client, runFolder);
            client.session().logout();
        }
    }

    /**
     * 创建源文件目录和解压目录，并上传两个本地临时文件。
     */
    private static List<String> prepareTestData(SynologyDsmClient client, String runFolder) throws IOException {
        String sourceFolder = appendPath(runFolder, "source");
        String extractedFolder = appendPath(runFolder, "extracted");
        client.fileStation().file().createFolder(
                CreateFolderRequest.builder()
                        .addFolder(runFolder, "source")
                        .addFolder(runFolder, "extracted")
                        .forceParent(Boolean.TRUE)
                        .build()
        );
        log.info("已准备远端测试目录：source={}，extracted={}", sourceFolder, extractedFolder);

        List<String> uploadedPaths = new ArrayList<String>();
        for (int index = 1; index <= 2; index++) {
            File tempFile = File.createTempFile("sdk-archive-" + index + "-", ".txt");
            try {
                FileUtil.writeUtf8String("Synology DSM Java SDK archive example file " + index, tempFile);
                UploadFileResponse response = client.fileStation().upload().file(
                        UploadFileRequest.builder(sourceFolder, tempFile)
                                .overwrite(Boolean.TRUE)
                                .createParents(Boolean.TRUE)
                                .build()
                );
                String remotePath = appendPath(sourceFolder, tempFile.getName());
                uploadedPaths.add(remotePath);
                log.info("已上传压缩源文件：{}，成功：{}", remotePath, response.isSuccess());
            } finally {
                FileUtil.del(tempFile);
            }
        }
        return uploadedPaths;
    }

    /**
     * 启动 zip 压缩任务，并复用 SDK 通用任务轮询器等待完成。
     */
    private static void compressDemo(SynologyDsmClient client, List<String> sourcePaths, String archivePath) {
        log.info("========== Compress 演示开始 ==========");
        CompressStartRequest.Builder requestBuilder = CompressStartRequest.builder(sourcePaths.get(0), archivePath)
                .level(CompressLevel.MODERATE)
                .mode(CompressMode.ADD)
                .format(CompressFormat.ZIP);
        for (int index = 1; index < sourcePaths.size(); index++) {
            requestBuilder.addPath(sourcePaths.get(index));
        }

        TaskStartResponse startResponse = client.fileStation().compress().start(requestBuilder.build());
        log.info("Compress 任务已启动：taskid={}", startResponse.getTaskid());
        CompressStatusResponse status = client.fileStation().compress()
                .wait(startResponse.getTaskid(), POLLING_OPTIONS);
        log.info("Compress 任务完成：finished={}，archive={}",
                status.getFinished(), status.getDestFilePath());
        log.info("========== Compress 演示结束 ==========");
    }

    /**
     * 列出 zip 根目录条目；itemId 可传给 Extract start 实现部分解压。
     */
    private static void listArchiveDemo(SynologyDsmClient client, String archivePath) {
        log.info("========== Extract list 演示开始 ==========");
        ExtractListResponse response = client.fileStation().extract().list(
                ExtractListRequest.builder(archivePath)
                        .limit(-1)
                        .sortBy("name")
                        .sortDirection(io.github.shuzhuoi.synology.filestation.option.SortDirection.ASC)
                        .build()
        );
        log.info("压缩包内部条目总数：{}", response.getTotal());
        if (response.getItems() != null) {
            for (ArchiveItem item : response.getItems()) {
                log.info("  - itemId={}，name={}，path={}，dir={}，size={}",
                        item.getItemId(), item.getName(), item.getPath(), item.getDir(), item.getSize());
            }
        }
        log.info("========== Extract list 演示结束 ==========");
    }

    /**
     * 解压 zip 的全部内容，并复用 SDK 通用任务轮询器等待完成。
     */
    private static void extractDemo(SynologyDsmClient client, String archivePath, String destFolder) {
        log.info("========== Extract 演示开始 ==========");
        ExtractStartRequest request = ExtractStartRequest.builder(archivePath, destFolder)
                .overwrite(Boolean.TRUE)
                .keepDir(Boolean.TRUE)
                .createSubfolder(Boolean.FALSE)
                .build();
        TaskStartResponse startResponse = client.fileStation().extract().start(request);
        log.info("Extract 任务已启动：taskid={}", startResponse.getTaskid());
        ExtractStatusResponse status = client.fileStation().extract()
                .wait(startResponse.getTaskid(), POLLING_OPTIONS);
        log.info("Extract 任务完成：finished={}，progress={}，destFolder={}",
                status.getFinished(), status.getProgress(), status.getDestFolderPath());
        log.info("========== Extract 演示结束 ==========");
    }

    /**
     * 删除本次运行的唯一远端目录；清理失败只记录警告，避免覆盖原始异常。
     */
    private static void cleanupTestData(SynologyDsmClient client, String runFolder) {
        log.info("========== 清理归档示例数据 ==========");
        try {
            client.fileStation().file().delete(
                    DeleteRequest.builder().addPath(runFolder).build()
            );
            log.info("本次运行目录已删除：{}", runFolder);
        } catch (Exception e) {
            log.warn("清理本次运行目录失败：{}，请手动检查。原因：{}", runFolder, e.getMessage());
        }
    }

    private static FileStationArchiveExampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = FileStationArchiveExample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, FileStationArchiveExampleConfig.class);
        }
    }

    private static String requiredConfigValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("示例配置文件 " + CONFIG_FILE + " 缺少必填配置：" + fieldName);
        }
        return value;
    }

    private static String appendPath(String parent, String child) {
        String normalizedParent = parent;
        while (normalizedParent.length() > 1 && normalizedParent.endsWith("/")) {
            normalizedParent = normalizedParent.substring(0, normalizedParent.length() - 1);
        }
        return normalizedParent + "/" + child;
    }
}
