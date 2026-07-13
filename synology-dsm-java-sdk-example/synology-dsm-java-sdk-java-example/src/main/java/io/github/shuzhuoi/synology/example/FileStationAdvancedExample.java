package io.github.shuzhuoi.synology.example;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.auth.store.InMemorySynologySessionStore;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.example.config.FileStationAdvancedExampleConfig;
import io.github.shuzhuoi.synology.filestation.backgroundtask.BackgroundTask;
import io.github.shuzhuoi.synology.filestation.backgroundtask.BackgroundTaskListRequest;
import io.github.shuzhuoi.synology.filestation.backgroundtask.BackgroundTaskListResponse;
import io.github.shuzhuoi.synology.filestation.option.SortDirection;
import io.github.shuzhuoi.synology.filestation.dirsize.DirSizeStartRequest;
import io.github.shuzhuoi.synology.filestation.dirsize.DirSizeStatusResponse;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderRequest;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderResponse;
import io.github.shuzhuoi.synology.filestation.file.DeleteRequest;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;
import io.github.shuzhuoi.synology.filestation.search.SearchListRequest;
import io.github.shuzhuoi.synology.filestation.search.SearchListResponse;
import io.github.shuzhuoi.synology.filestation.search.SearchStartRequest;
import io.github.shuzhuoi.synology.filestation.task.TaskPollingOptions;
import io.github.shuzhuoi.synology.filestation.task.TaskStartResponse;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileRequest;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileResponse;
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
 * v0.2.0 高级示例。
 * <p>
 * 演示 Search / DirSize / BackgroundTask 三项新增能力，并展示 autoRefreshSession 配置项。
 * 运行前请复制 classpath 下的 filestation-advanced.example.yaml 为 filestation-advanced.yaml，
 * 并填写真实 DSM 地址、账号、密码和远端测试目录。
 * <p>
 * 为避免污染用户已有数据，示例会在 sampleFolder 下创建子目录并上传临时文件，结束时统一清理。
 */
@Slf4j
public class FileStationAdvancedExample {

    private static final String CONFIG_FILE = "filestation-advanced.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "filestation-advanced.example.yaml";

    /**
     * 演示用临时文件的前缀，便于 Search 演示时按前缀匹配。
     */
    private static final String TEMP_FILE_PREFIX = "sdk-advanced-";

    /**
     * 轮询配置：500ms 间隔、最多 120 次（约 60 秒），覆盖 Search 和 DirSize 的典型耗时。
     */
    private static final TaskPollingOptions POLLING_OPTIONS = TaskPollingOptions.builder()
            .intervalMillis(500L)
            .maxAttempts(120)
            .build();

    public static void main(String[] args) throws IOException {
        FileStationAdvancedExampleConfig sampleConfig = readSampleConfig();
        // 显式开启 autoRefreshSession：当认证请求遇到 106/107/119 会话失效错误码时，
        // SDK 会自动重新登录并重试一次，避免长连接场景下因 SID 过期中断业务。
        // 这里仅为展示配置项，示例运行过程中一般不会真正触发。
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(requiredConfigValue(sampleConfig.getDsmUrl(), "dsmUrl"))
                .account(requiredConfigValue(sampleConfig.getAccount(), "account"))
                .password(requiredConfigValue(sampleConfig.getPassword(), "password"))
                .autoRefreshSession(Boolean.TRUE)
                .build();

        // 显式传入默认本地 Map session store；不传时 SDK 也会创建同样的默认实现。
        // 如果用户需要 Redis、Caffeine 或数据库缓存，可自行实现 SynologySessionStore 后在这里注入。
        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(
                config,
                new JacksonSynologyJsonCodec(),
                new InMemorySynologySessionStore()
        );
        String remoteFolder = requiredConfigValue(sampleConfig.getSampleFolder(), "sampleFolder");

        try {
            // 步骤 1：准备测试数据，上传若干临时文件供后续搜索和统计使用。
            List<String> uploadedPaths = prepareTestData(client, remoteFolder);

            // 步骤 2：演示 SYNO.FileStation.Search。
            searchDemo(client, remoteFolder);

            // 步骤 3：演示 SYNO.FileStation.DirSize。
            dirSizeDemo(client, remoteFolder);

            // 步骤 4：演示 SYNO.FileStation.BackgroundTask。
            backgroundTaskDemo(client, uploadedPaths);
        } finally {
            // 步骤 5：清理测试数据，删除示例过程中创建的临时文件。
            cleanupTestData(client, remoteFolder);
            client.session().logout();
        }
    }

    /**
     * 准备测试数据：在 sampleFolder 下创建子目录并上传几个临时文件。
     * <p>
     * 使用统一前缀 {@link #TEMP_FILE_PREFIX} 便于后续 Search 演示按前缀匹配。
     */
    private static List<String> prepareTestData(SynologyDsmClient client, String remoteFolder) throws IOException {
        // 确保远端目录存在，forceParent=true 时会自动创建父目录。
        CreateFolderResponse folder = client.fileStation().file().createFolder(
                CreateFolderRequest.builder()
                        .addFolder(parentPath(remoteFolder), folderName(remoteFolder))
                        .forceParent(Boolean.TRUE)
                        .build()
        );
        log.info("准备测试目录：{}", folder.getFolders().get(0).getPath());

        List<String> uploadedPaths = new ArrayList<String>();
        String[] fileNames = {TEMP_FILE_PREFIX + "alpha.txt", TEMP_FILE_PREFIX + "beta.txt", TEMP_FILE_PREFIX + "gamma.txt"};
        for (String fileName : fileNames) {
            File tempFile = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
            FileUtil.writeUtf8String("v0.2.0 advanced example: " + fileName, tempFile);
            UploadFileResponse uploadResponse = client.fileStation().upload().file(
                    UploadFileRequest.builder(remoteFolder, tempFile)
                            .overwrite(Boolean.TRUE)
                            .createParents(Boolean.TRUE)
                            .build()
            );
            String remotePath = remoteFolder + "/" + fileName;
            uploadedPaths.add(remotePath);
            log.info("上传临时文件：{}，成功：{}", remotePath, uploadResponse.isSuccess());
            // 上传完成后删除本地临时文件，避免残留。
            FileUtil.del(tempFile);
        }
        return uploadedPaths;
    }

    /**
     * 演示 SYNO.FileStation.Search：按文件名前缀搜索，轮询 list 直到完成，最后清理临时数据库。
     */
    private static void searchDemo(SynologyDsmClient client, String remoteFolder) {
        log.info("========== Search 演示开始 ==========");

        // start：在 remoteFolder 下递归搜索文件名匹配 sdk-advanced-*.txt 的文件。
        SearchStartRequest startRequest = SearchStartRequest.builder(remoteFolder)
                .recursive(Boolean.TRUE)
                .pattern(TEMP_FILE_PREFIX + "*.txt")
                .filetype("file")
                .build();
        TaskStartResponse startResponse = client.fileStation().search().start(startRequest);
        String taskId = startResponse.getTaskid();
        log.info("Search 任务已启动：taskid={}", taskId);

        // 手动 list 一次，展示 finished 标记。搜索小目录通常立即完成。
        SearchListRequest listRequest = SearchListRequest.builder(taskId)
                .limit(50)
                .addAdditional(Additional.SIZE)
                .addAdditional(Additional.TIME)
                .build();
        SearchListResponse firstPeek = client.fileStation().search().list(listRequest);
        log.info("首次 list 探测：finished={}，当前匹配数={}", firstPeek.getFinished(), firstPeek.getTotal());

        // 若未完成则用 wait 轮询直到完成。wait 内部复用通用轮询器 SynologyTaskPoller。
        SearchListResponse finalResult;
        if (Boolean.TRUE.equals(firstPeek.getFinished())) {
            finalResult = firstPeek;
        } else {
            log.info("搜索尚未完成，开始轮询等待...");
            finalResult = client.fileStation().search().wait(taskId, POLLING_OPTIONS);
        }

        log.info("搜索完成，共匹配 {} 个文件：", finalResult.getFiles() == null ? 0 : finalResult.getFiles().size());
        if (finalResult.getFiles() != null) {
            for (SynologyFile file : finalResult.getFiles()) {
                log.info("  - {}", file.getPath());
            }
        }

        // clean 释放 DSM 端临时数据库，避免资源泄漏。stop 只取消搜索不清临时库，这里用 clean。
        client.fileStation().search().clean(taskId);
        log.info("Search 临时数据库已清理：taskid={}", taskId);
        log.info("========== Search 演示结束 ==========");
    }

    /**
     * 演示 SYNO.FileStation.DirSize：计算目录累计大小，轮询 status 直到完成。
     */
    private static void dirSizeDemo(SynologyDsmClient client, String remoteFolder) {
        log.info("========== DirSize 演示开始 ==========");

        // start：统计 remoteFolder 下的目录数、文件数和累计大小。
        DirSizeStartRequest startRequest = DirSizeStartRequest.builder(remoteFolder).build();
        TaskStartResponse startResponse = client.fileStation().dirSize().start(startRequest);
        String taskId = startResponse.getTaskid();
        log.info("DirSize 任务已启动：taskid={}", taskId);

        // wait 便捷方法内部复用 SynologyTaskPoller，轮询 status 直到 finished=true。
        DirSizeStatusResponse result = client.fileStation().dirSize().wait(taskId, POLLING_OPTIONS);
        log.info("DirSize 计算完成：目录数={}，文件数={}，累计大小={} 字节",
                result.getNumDir(), result.getNumFile(), result.getTotalSize());
        log.info("========== DirSize 演示结束 ==========");
    }

    /**
     * 演示 SYNO.FileStation.BackgroundTask：触发一个异步删除任务，列出后台任务，清理已完成任务。
     * <p>
     * 注意：具体任务（CopyMove/Delete 等）的进度查询和取消仍在各自客户端完成，
     * BackgroundTask 只提供全局视角的列表和批量清理。
     */
    private static void backgroundTaskDemo(SynologyDsmClient client, List<String> uploadedPaths) {
        log.info("========== BackgroundTask 演示开始 ==========");

        // 触发一个异步删除任务（deleteAsync 不等待完成），用于在后台任务列表中产生一条记录。
        // 这里删除 uploadedPaths 中的第一个文件作为演示。
        if (!uploadedPaths.isEmpty()) {
            String pathToDelete = uploadedPaths.get(0);
            client.fileStation().file().deleteAsync(
                    DeleteRequest.builder().addPath(pathToDelete).build()
            );
            log.info("已触发异步删除任务：{}", pathToDelete);
            // 等待短暂时间让任务进入后台任务列表。
            sleepQuiet(1000L);
        }

        // list：查看当前所有后台任务，可按 API 名称过滤。
        BackgroundTaskListRequest listRequest = BackgroundTaskListRequest.builder()
                .limit(20)
                .sortBy("crtime")
                .sortDirection(SortDirection.DESC)
                .addApiFilter("SYNO.FileStation.Delete")
                .build();
        BackgroundTaskListResponse listResponse = client.fileStation().backgroundTask().list(listRequest);
        log.info("后台任务列表（共 {} 条）：", listResponse.getTotal());
        if (listResponse.getTasks() != null) {
            for (BackgroundTask task : listResponse.getTasks()) {
                log.info("  - taskid={}，api={}，finished={}，progress={}",
                        task.getTaskid(), task.getApi(), task.getFinished(), task.getProgress());
            }
        }

        // clearFinished：清空所有已完成的后台任务，释放 DSM 端任务记录。
        // 也可传入指定 taskid 列表只清理部分任务。
        client.fileStation().backgroundTask().clearFinished();
        log.info("已清空所有已完成的后台任务");
        log.info("========== BackgroundTask 演示结束 ==========");
    }

    /**
     * 清理测试数据：删除 sampleFolder 下的所有临时文件。
     * <p>
     * 使用同步 delete 确保清理完成，避免残留影响下次运行。
     */
    private static void cleanupTestData(SynologyDsmClient client, String remoteFolder) {
        log.info("========== 清理测试数据 ==========");
        try {
            // 删除整个测试目录，SDK 内部会启动异步删除任务并轮询等待完成。
            client.fileStation().file().delete(
                    DeleteRequest.builder().addPath(remoteFolder).build()
            );
            log.info("测试目录已删除：{}", remoteFolder);
        } catch (Exception e) {
            // 清理失败不阻断流程，仅记录警告，用户可手动检查。
            log.warn("清理测试目录失败：{}，请手动检查。原因：{}", remoteFolder, e.getMessage());
        }
    }

    private static FileStationAdvancedExampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = FileStationAdvancedExample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, FileStationAdvancedExampleConfig.class);
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
