package io.github.shuzhuoi.synology.filestation;

import io.github.shuzhuoi.synology.filestation.backgroundtask.FileStationBackgroundTaskClient;
import io.github.shuzhuoi.synology.filestation.dirsize.FileStationDirSizeClient;
import io.github.shuzhuoi.synology.filestation.download.FileStationDownloadClient;
import io.github.shuzhuoi.synology.filestation.file.FileStationFileClient;
import io.github.shuzhuoi.synology.filestation.info.FileStationInfoClient;
import io.github.shuzhuoi.synology.filestation.list.FileStationListClient;
import io.github.shuzhuoi.synology.filestation.search.FileStationSearchClient;
import io.github.shuzhuoi.synology.filestation.task.FileStationTaskClient;
import io.github.shuzhuoi.synology.filestation.upload.FileStationUploadClient;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

/**
 * File Station API 聚合入口。
 * <p>
 * 该类不直接发请求，只负责把 File Station 的能力按资源类型拆分到不同客户端中。
 */
public class FileStationClient {

    /**
     * File Station 基础信息接口。
     */
    private final FileStationInfoClient infoClient;
    /**
     * 共享目录、文件列表、文件详情接口。
     */
    private final FileStationListClient listClient;
    /**
     * 文件上传接口。
     */
    private final FileStationUploadClient uploadClient;
    /**
     * 文件下载接口。
     */
    private final FileStationDownloadClient downloadClient;
    /**
     * 创建目录、重命名、删除、复制、移动等文件操作接口。
     */
    private final FileStationFileClient fileClient;
    /**
     * MD5 等任务型接口。
     */
    private final FileStationTaskClient taskClient;
    /**
     * 文件搜索接口。
     */
    private final FileStationSearchClient searchClient;
    /**
     * 目录大小统计接口。
     */
    private final FileStationDirSizeClient dirSizeClient;
    /**
     * 后台任务列表与清理接口。
     */
    private final FileStationBackgroundTaskClient backgroundTaskClient;

    public FileStationClient(SynologyApiExecutor executor) {
        this.infoClient = new FileStationInfoClient(executor);
        this.listClient = new FileStationListClient(executor);
        this.uploadClient = new FileStationUploadClient(executor);
        this.downloadClient = new FileStationDownloadClient(executor);
        this.fileClient = new FileStationFileClient(executor);
        this.taskClient = new FileStationTaskClient(executor);
        this.searchClient = new FileStationSearchClient(executor);
        this.dirSizeClient = new FileStationDirSizeClient(executor);
        this.backgroundTaskClient = new FileStationBackgroundTaskClient(executor);
    }

    public FileStationInfoClient info() {
        return infoClient;
    }

    public FileStationListClient list() {
        return listClient;
    }

    public FileStationUploadClient upload() {
        return uploadClient;
    }

    public FileStationDownloadClient download() {
        return downloadClient;
    }

    public FileStationFileClient file() {
        return fileClient;
    }

    public FileStationTaskClient task() {
        return taskClient;
    }

    public FileStationSearchClient search() {
        return searchClient;
    }

    public FileStationDirSizeClient dirSize() {
        return dirSizeClient;
    }

    public FileStationBackgroundTaskClient backgroundTask() {
        return backgroundTaskClient;
    }
}
