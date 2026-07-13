package io.github.shuzhuoi.synology.example;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.shuzhuoi.synology.client.SynologyDsmClient;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.example.config.FileStationResourceExampleConfig;
import io.github.shuzhuoi.synology.filestation.favorite.Favorite;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteAddRequest;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteEditRequest;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteListRequest;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteListResponse;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderRequest;
import io.github.shuzhuoi.synology.filestation.file.DeleteRequest;
import io.github.shuzhuoi.synology.filestation.sharing.SharingCreateRequest;
import io.github.shuzhuoi.synology.filestation.sharing.SharingCreateResponse;
import io.github.shuzhuoi.synology.filestation.sharing.SharingEditRequest;
import io.github.shuzhuoi.synology.filestation.sharing.SharingLink;
import io.github.shuzhuoi.synology.filestation.sharing.SharingListRequest;
import io.github.shuzhuoi.synology.filestation.sharing.SharingListResponse;
import io.github.shuzhuoi.synology.filestation.thumb.ThumbGetRequest;
import io.github.shuzhuoi.synology.filestation.thumb.ThumbSize;
import io.github.shuzhuoi.synology.filestation.option.SortDirection;
import io.github.shuzhuoi.synology.filestation.thumb.ThumbGetResponse;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileRequest;
import io.github.shuzhuoi.synology.filestation.virtualfolder.VirtualFolder;
import io.github.shuzhuoi.synology.filestation.virtualfolder.VirtualFolderListRequest;
import io.github.shuzhuoi.synology.filestation.virtualfolder.VirtualFolderListResponse;
import io.github.shuzhuoi.synology.http.hutool.HutoolSynologyDsmClientFactory;
import io.github.shuzhuoi.synology.json.jackson.JacksonSynologyJsonCodec;
import io.github.shuzhuoi.synology.model.Additional;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * File Station 资源能力示例。
 * <p>
 * 演示 Sharing / Favorite / Thumb / VirtualFolder 四项新增能力。
 * 运行前请复制 classpath 下的 filestation-resource.example.yaml 为 filestation-resource.yaml，
 * 并填写真实 DSM 地址、账号、密码、远端测试目录和本地下载目录。
 */
@Slf4j
public class FileStationResourceExample {

    private static final String CONFIG_FILE = "filestation-resource.yaml";
    private static final String CONFIG_EXAMPLE_FILE = "filestation-resource.example.yaml";
    private static final String TEMP_IMAGE_NAME = "sdk-resource-thumb.png";
    /**
     * 1x1 PNG，用于 Thumb 和 Sharing 演示，避免依赖用户本地图片文件。
     */
    private static final String ONE_PIXEL_PNG_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/p9sAAAAASUVORK5CYII=";

    public static void main(String[] args) throws IOException {
        FileStationResourceExampleConfig sampleConfig = readSampleConfig();
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl(requiredConfigValue(sampleConfig.getDsmUrl(), "dsmUrl"))
                .account(requiredConfigValue(sampleConfig.getAccount(), "account"))
                .password(requiredConfigValue(sampleConfig.getPassword(), "password"))
                .autoRefreshSession(Boolean.TRUE)
                .build();

        SynologyDsmClient client = HutoolSynologyDsmClientFactory.create(config, new JacksonSynologyJsonCodec());
        String remoteFolder = requiredConfigValue(sampleConfig.getSampleFolder(), "sampleFolder");
        File downloadFolder = FileUtil.mkdir(requiredConfigValue(sampleConfig.getDownloadFolder(), "downloadFolder"));
        String virtualFolderType = requiredConfigValue(sampleConfig.getVirtualFolderType(), "virtualFolderType");
        String sharingPassword = optionalConfigValue(sampleConfig.getSharingPassword(), "sdkResource");
        String remoteImagePath = remoteFolder + "/" + TEMP_IMAGE_NAME;

        try {
            prepareTestData(client, remoteFolder);
            File localImage = createLocalDemoImage();
            client.fileStation().upload().file(
                    UploadFileRequest.builder(remoteFolder, localImage)
                            .overwrite(Boolean.TRUE)
                            .createParents(Boolean.TRUE)
                            .build()
            );
            FileUtil.del(localImage);
            log.info("上传资源能力演示图片：{}", remoteImagePath);

            favoriteDemo(client, remoteFolder);
            virtualFolderDemo(client, virtualFolderType);
            thumbDemo(client, remoteImagePath, downloadFolder);
            sharingDemo(client, remoteImagePath, sharingPassword);
        } finally {
            cleanupTestData(client, remoteImagePath);
            client.session().logout();
        }
    }

    /**
     * 确保远端测试目录存在。
     */
    private static void prepareTestData(SynologyDsmClient client, String remoteFolder) {
        client.fileStation().file().createFolder(
                CreateFolderRequest.builder()
                        .addFolder(parentPath(remoteFolder), folderName(remoteFolder))
                        .forceParent(Boolean.TRUE)
                        .build()
        );
        log.info("准备资源能力测试目录：{}", remoteFolder);
    }

    /**
     * 演示 SYNO.FileStation.Favorite。
     */
    private static void favoriteDemo(SynologyDsmClient client, String remoteFolder) {
        log.info("========== Favorite 演示开始 ==========");
        String favoriteName = "sdk-resource-favorite";
        String editedName = "sdk-resource-favorite-edited";

        deleteFavoriteQuietly(client, remoteFolder);
        client.fileStation().favorite().add(
                FavoriteAddRequest.builder(remoteFolder, favoriteName)
                        .index(-1)
                        .build()
        );
        log.info("新增收藏：{} -> {}", favoriteName, remoteFolder);

        FavoriteListResponse listResponse = client.fileStation().favorite().list(
                FavoriteListRequest.builder()
                        .limit(20)
                        .statusFilter("all")
                        .addAdditional(Additional.REAL_PATH)
                        .addAdditional(Additional.TIME)
                        .build()
        );
        log.info("收藏夹列表（共 {} 条）：", listResponse.getTotal());
        for (Favorite favorite : listResponse.getFavorites()) {
            log.info("  - name={}，path={}，status={}", favorite.getName(), favorite.getPath(), favorite.getStatus());
        }

        client.fileStation().favorite().edit(FavoriteEditRequest.builder(remoteFolder, editedName).build());
        log.info("编辑收藏名称：{} -> {}", favoriteName, editedName);

        client.fileStation().favorite().delete(remoteFolder);
        client.fileStation().favorite().clearBroken();
        log.info("删除收藏并清理 broken 收藏：{}", remoteFolder);
        log.info("========== Favorite 演示结束 ==========");
    }

    /**
     * 示例可能因为上次中断残留收藏，正式 add 前先尝试清理一次。
     */
    private static void deleteFavoriteQuietly(SynologyDsmClient client, String remoteFolder) {
        try {
            client.fileStation().favorite().delete(remoteFolder);
        } catch (Exception ignored) {
            // 没有旧收藏时 DSM 会返回错误，这里忽略，让后续 add 展示主流程。
        }
    }

    /**
     * 演示 SYNO.FileStation.VirtualFolder。
     */
    private static void virtualFolderDemo(SynologyDsmClient client, String virtualFolderType) {
        log.info("========== VirtualFolder 演示开始 ==========");
        VirtualFolderListResponse response = client.fileStation().virtualFolder().list(
                VirtualFolderListRequest.builder(virtualFolderType)
                        .limit(20)
                        .addAdditional(Additional.REAL_PATH)
                        .addAdditional(Additional.MOUNT_POINT_TYPE)
                        .addAdditional(Additional.VOLUME_STATUS)
                        .build()
        );
        log.info("{} 虚拟目录列表（共 {} 条）：", virtualFolderType, response.getTotal());
        for (VirtualFolder folder : response.getFolders()) {
            log.info("  - name={}，path={}", folder.getName(), folder.getPath());
        }
        log.info("========== VirtualFolder 演示结束 ==========");
    }

    /**
     * 演示 SYNO.FileStation.Thumb：获取缩略图并保存到本地目录。
     */
    private static void thumbDemo(SynologyDsmClient client, String remoteImagePath, File downloadFolder) throws IOException {
        log.info("========== Thumb 演示开始 ==========");
        ThumbGetResponse response = client.fileStation().thumb().get(
                ThumbGetRequest.builder(remoteImagePath)
                            .size(ThumbSize.SMALL)
                        .rotate(0)
                        .build()
        );
        File thumbFile = FileUtil.file(downloadFolder, "thumb-" + TEMP_IMAGE_NAME);
        saveStream(response.getInputStream(), thumbFile);
        log.info("缩略图已保存：{}，HTTP 状态：{}", thumbFile.getAbsolutePath(), response.getStatusCode());
        log.info("========== Thumb 演示结束 ==========");
    }

    /**
     * 演示 SYNO.FileStation.Sharing。
     */
    private static void sharingDemo(SynologyDsmClient client, String remoteImagePath, String sharingPassword) {
        log.info("========== Sharing 演示开始 ==========");
        SharingCreateResponse createResponse = client.fileStation().sharing().create(
                SharingCreateRequest.builder(remoteImagePath)
                        .password(sharingPassword)
                        .dateExpired("0")
                        .dateAvailable("0")
                        .build()
        );
        if (createResponse.getLinks().isEmpty()) {
            throw new IllegalStateException("Sharing create 未返回链接信息。");
        }
        SharingLink created = createResponse.getLinks().get(0);
        log.info("创建分享链接：id={}，url={}，error={}", created.getId(), created.getUrl(), created.getError());

        SharingListResponse listResponse = client.fileStation().sharing().list(
                SharingListRequest.builder()
                        .limit(20)
                        .sortBy("name")
                        .sortDirection(SortDirection.ASC)
                        .forceClean(Boolean.TRUE)
                        .build()
        );
        log.info("分享链接列表（共 {} 条）", listResponse.getTotal());

        SharingLink info = client.fileStation().sharing().getInfo(created.getId());
        log.info("分享链接详情：id={}，path={}，status={}", info.getId(), info.getPath(), info.getStatus());

        client.fileStation().sharing().edit(
                SharingEditRequest.builder(created.getId())
                        .password("")
                        .dateExpired("0")
                        .dateAvailable("0")
                        .build()
        );
        log.info("已移除分享链接密码：{}", created.getId());

        client.fileStation().sharing().delete(created.getId());
        client.fileStation().sharing().clearInvalid();
        log.info("删除分享链接并清理无效分享：{}", created.getId());
        log.info("========== Sharing 演示结束 ==========");
    }

    /**
     * 清理示例上传的临时图片。
     */
    private static void cleanupTestData(SynologyDsmClient client, String remoteImagePath) {
        try {
            client.fileStation().file().delete(DeleteRequest.builder().addPath(remoteImagePath).build());
            log.info("清理资源能力临时图片：{}", remoteImagePath);
        } catch (Exception e) {
            log.warn("清理资源能力临时图片失败：{}，原因：{}", remoteImagePath, e.getMessage());
        }
    }

    private static FileStationResourceExampleConfig readSampleConfig() throws IOException {
        InputStream inputStream = FileStationResourceExample.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream == null) {
            throw new IllegalArgumentException("未找到示例配置文件 " + CONFIG_FILE
                    + "，请先复制 " + CONFIG_EXAMPLE_FILE + " 为 " + CONFIG_FILE + " 后再运行。");
        }
        try (InputStream configInputStream = inputStream) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(configInputStream, FileStationResourceExampleConfig.class);
        }
    }

    private static File createLocalDemoImage() {
        File imageFile = FileUtil.file(System.getProperty("java.io.tmpdir"), TEMP_IMAGE_NAME);
        byte[] imageBytes = Base64.getDecoder().decode(ONE_PIXEL_PNG_BASE64);
        FileUtil.writeBytes(imageBytes, imageFile);
        return imageFile;
    }

    private static void saveStream(InputStream inputStream, File targetFile) throws IOException {
        if (inputStream == null) {
            throw new IllegalStateException("响应中没有可读取的文件流。");
        }
        try (InputStream stream = inputStream) {
            FileUtil.writeFromStream(stream, targetFile);
        }
    }

    private static String requiredConfigValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("示例配置文件 " + CONFIG_FILE + " 缺少必填配置：" + fieldName);
        }
        return value;
    }

    private static String optionalConfigValue(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
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
}
