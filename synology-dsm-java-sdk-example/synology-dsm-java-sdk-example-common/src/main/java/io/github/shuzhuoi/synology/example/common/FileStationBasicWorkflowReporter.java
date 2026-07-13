package io.github.shuzhuoi.synology.example.common;

import java.util.function.Consumer;

/**
 * 统一输出基础工作流结果，避免普通 Java 和两个 Spring Boot 示例重复拼接日志。
 */
public final class FileStationBasicWorkflowReporter {

    private FileStationBasicWorkflowReporter() {
    }

    public static void report(FileStationBasicWorkflowResult result, Consumer<String> output) {
        output.accept("目录文件列表：");
        for (String path : result.getListedPaths()) {
            output.accept(path);
        }
        output.accept("创建目录：" + result.getCreatedFolderPath());
        output.accept("上传文件：" + result.getUploadedPath() + "，成功：" + result.isUploadSuccess());
        output.accept("下载文件：" + result.getDownloadedFilePath());
        output.accept("重命名文件：" + result.getRenamedPath());
        output.accept("删除文件：" + result.getRenamedPath() + "，成功：" + result.isDeleteSuccess());
    }
}
