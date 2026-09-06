package io.github.shuzhuoi.synology.filestation;

import io.github.shuzhuoi.synology.auth.AuthClient;
import io.github.shuzhuoi.synology.auth.SynologySession;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.filestation.backgroundtask.BackgroundTaskListRequest;
import io.github.shuzhuoi.synology.filestation.compress.CompressFormat;
import io.github.shuzhuoi.synology.filestation.compress.CompressLevel;
import io.github.shuzhuoi.synology.filestation.compress.CompressMode;
import io.github.shuzhuoi.synology.filestation.compress.CompressStartRequest;
import io.github.shuzhuoi.synology.filestation.dirsize.DirSizeStartRequest;
import io.github.shuzhuoi.synology.filestation.extract.ExtractListRequest;
import io.github.shuzhuoi.synology.filestation.extract.ExtractStartRequest;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteAddRequest;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteEditRequest;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteListRequest;
import io.github.shuzhuoi.synology.filestation.favorite.FavoriteReplaceAllRequest;
import io.github.shuzhuoi.synology.filestation.file.CopyMoveRequest;
import io.github.shuzhuoi.synology.filestation.file.CreateFolderRequest;
import io.github.shuzhuoi.synology.filestation.file.DeleteRequest;
import io.github.shuzhuoi.synology.filestation.file.RenameRequest;
import io.github.shuzhuoi.synology.filestation.list.GetFileInfoRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.list.ListSharesRequest;
import io.github.shuzhuoi.synology.filestation.option.FileTypeFilter;
import io.github.shuzhuoi.synology.filestation.option.SortDirection;
import io.github.shuzhuoi.synology.filestation.permission.CheckPermissionWriteRequest;
import io.github.shuzhuoi.synology.filestation.permission.CheckPermissionWriteResponse;
import io.github.shuzhuoi.synology.filestation.search.SearchListRequest;
import io.github.shuzhuoi.synology.filestation.search.SearchStartRequest;
import io.github.shuzhuoi.synology.filestation.sharing.SharingCreateRequest;
import io.github.shuzhuoi.synology.filestation.sharing.SharingEditRequest;
import io.github.shuzhuoi.synology.filestation.sharing.SharingListRequest;
import io.github.shuzhuoi.synology.filestation.task.Md5Request;
import io.github.shuzhuoi.synology.filestation.thumb.ThumbGetRequest;
import io.github.shuzhuoi.synology.filestation.thumb.ThumbSize;
import io.github.shuzhuoi.synology.filestation.upload.UploadFileRequest;
import io.github.shuzhuoi.synology.filestation.upload.UploadOverwritePolicy;
import io.github.shuzhuoi.synology.filestation.virtualfolder.VirtualFolderListRequest;
import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;
import io.github.shuzhuoi.synology.model.Additional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * File Station 各子客户端的契约测试。
 * <p>
 * 通过记录型 HTTP fake 捕获最终发出的请求，逐个断言各客户端使用的
 * API 名称、版本、method 和关键参数编码（JSON-like 数组、引号包裹、布尔编码等），
 * 防止官方 WebAPI 契约在重构或新增枚举重载时被意外破坏。
 */
class FileStationClientContractTest {

    private static final String SID = "sid-test";

    private RecordingSynologyHttpClient httpClient;
    private FileStationClient fileStation;
    private File tempUploadFile;

    @BeforeEach
    void setUp() throws IOException {
        SynologyDsmConfig config = SynologyDsmConfig.builder()
                .baseUrl("http://nas:5000")
                .build();
        httpClient = new RecordingSynologyHttpClient();
        SynologyApiExecutor executor = new SynologyApiExecutor(config, httpClient, new SuccessJsonCodec());
        executor.setSessionManager(new FixedSidSessionManager(config));
        fileStation = new FileStationClient(executor);
        tempUploadFile = File.createTempFile("sdk-contract-", ".txt");
        Files.write(tempUploadFile.toPath(), "contract test".getBytes("UTF-8"));
    }

    @AfterEach
    void tearDown() {
        if (tempUploadFile != null && tempUploadFile.exists()) {
            tempUploadFile.delete();
        }
    }

    @Test
    void infoGetUsesInfoApi() {
        fileStation.info().get();

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Info", "2", "get");
    }

    @Test
    void listSharesUsesListShareMethod() {
        fileStation.list().shares(ListSharesRequest.builder()
                .offset(0)
                .limit(10)
                .sortBy("name")
                .sortDirection(SortDirection.ASC)
                .onlyWritable(true)
                .addAdditional(Additional.REAL_PATH)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.FileStation.List", "2", "list_share");
        assertEquals("0", request.getParameters().get("offset"));
        assertEquals("10", request.getParameters().get("limit"));
        assertEquals("name", request.getParameters().get("sort_by"));
        assertEquals("asc", request.getParameters().get("sort_direction"));
        assertEquals("true", request.getParameters().get("onlywritable"));
        assertEquals("[\"real_path\"]", request.getParameters().get("additional"));
    }

    @Test
    void listFilesEncodesFolderPathAndPaging() {
        fileStation.list().files(ListFilesRequest.builder("/home")
                .limit(50)
                .pattern("*.txt")
                .filetype(FileTypeFilter.FILE)
                .addAdditional(Additional.SIZE)
                .addAdditional(Additional.TIME)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.FileStation.List", "2", "list");
        assertEquals("\"/home\"", request.getParameters().get("folder_path"));
        assertEquals("50", request.getParameters().get("limit"));
        assertEquals("*.txt", request.getParameters().get("pattern"));
        assertEquals("file", request.getParameters().get("filetype"));
        assertEquals("[\"size\",\"time\"]", request.getParameters().get("additional"));
    }

    @Test
    void listGetInfoEncodesPathsAsArray() {
        fileStation.list().info(GetFileInfoRequest.builder()
                .addPath("/home/a.txt")
                .addPath("/home/b.txt")
                .addAdditional(Additional.OWNER)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.FileStation.List", "2", "getinfo");
        assertEquals("[\"/home/a.txt\",\"/home/b.txt\"]", request.getParameters().get("path"));
        assertEquals("[\"owner\"]", request.getParameters().get("additional"));
    }

    @Test
    void searchStartEncodesFolderPathsAndConditions() {
        fileStation.search().start(SearchStartRequest.builder("/docs")
                .addFolderPath("/photo")
                .recursive(true)
                .pattern("*.pdf")
                .filetype(FileTypeFilter.FILE)
                .sizeFrom(1024L)
                .sizeTo(4096L)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.FileStation.Search", "2", "start");
        assertEquals("[\"/docs\",\"/photo\"]", request.getParameters().get("folder_path"));
        assertEquals("true", request.getParameters().get("recursive"));
        assertEquals("*.pdf", request.getParameters().get("pattern"));
        assertEquals("file", request.getParameters().get("filetype"));
        assertEquals("1024", request.getParameters().get("size_from"));
        assertEquals("4096", request.getParameters().get("size_to"));
    }

    @Test
    void searchListQuotesTaskid() {
        fileStation.search().list(SearchListRequest.builder("tid-1").limit(20).build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.FileStation.Search", "2", "list");
        assertEquals("\"tid-1\"", request.getParameters().get("taskid"));
        assertEquals("20", request.getParameters().get("limit"));
    }

    @Test
    void searchStopAndCleanEncodeTaskids() {
        fileStation.search().stop(Arrays.asList("t1", "t2"));

        SynologyHttpRequest stopRequest = httpClient.getLastRequest();
        assertApi(stopRequest, "SYNO.FileStation.Search", "2", "stop");
        assertEquals("[\"t1\",\"t2\"]", stopRequest.getParameters().get("taskid"));

        fileStation.search().clean("t1");

        SynologyHttpRequest cleanRequest = httpClient.getLastRequest();
        assertApi(cleanRequest, "SYNO.FileStation.Search", "2", "clean");
        assertEquals("\"t1\"", cleanRequest.getParameters().get("taskid"));
    }

    @Test
    void checkPermissionWriteEncodesQuotedPathAndFilename() {
        CheckPermissionWriteResponse response = fileStation.permission().write(
                CheckPermissionWriteRequest.builder("/home", "a.txt")
                        .overwrite(true)
                        .build()
        );

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.FileStation.CheckPermission", "3", "write");
        assertEquals("\"/home\"", request.getParameters().get("path"));
        assertEquals("\"a.txt\"", request.getParameters().get("filename"));
        assertEquals("true", request.getParameters().get("overwrite"));
        // createOnly 未显式设置时使用官方默认 true。
        assertEquals("true", request.getParameters().get("create_only"));
        assertTrue(response.isAllowed());
    }

    @Test
    void dirSizeStartStatusAndStop() {
        fileStation.dirSize().start(DirSizeStartRequest.builder("/home").addPath("/photo").build());

        SynologyHttpRequest startRequest = httpClient.getLastRequest();
        assertApi(startRequest, "SYNO.FileStation.DirSize", "2", "start");
        assertEquals("[\"/home\",\"/photo\"]", startRequest.getParameters().get("path"));

        fileStation.dirSize().status("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.DirSize", "2", "status");
        assertEquals("\"tid\"", httpClient.getLastRequest().getParameters().get("taskid"));

        fileStation.dirSize().stop("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.DirSize", "2", "stop");
        assertEquals("\"tid\"", httpClient.getLastRequest().getParameters().get("taskid"));
    }

    @Test
    void md5TaskUsesMd5Api() {
        fileStation.task().md5(Md5Request.builder("/home/a.txt").build());

        SynologyHttpRequest startRequest = httpClient.getLastRequest();
        assertApi(startRequest, "SYNO.FileStation.MD5", "2", "start");
        assertEquals("\"/home/a.txt\"", startRequest.getParameters().get("file_path"));

        fileStation.task().md5Status("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.MD5", "2", "status");

        fileStation.task().md5Stop("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.MD5", "2", "stop");
    }

    @Test
    void backgroundTaskListAndClearFinished() {
        fileStation.backgroundTask().list(BackgroundTaskListRequest.builder()
                .limit(10)
                .addApiFilter("SYNO.FileStation.CopyMove")
                .build());

        SynologyHttpRequest listRequest = httpClient.getLastRequest();
        assertApi(listRequest, "SYNO.FileStation.BackgroundTask", "3", "list");
        assertEquals("10", listRequest.getParameters().get("limit"));
        assertEquals("[\"SYNO.FileStation.CopyMove\"]", listRequest.getParameters().get("api_filter"));

        fileStation.backgroundTask().clearFinished();

        SynologyHttpRequest clearAllRequest = httpClient.getLastRequest();
        assertApi(clearAllRequest, "SYNO.FileStation.BackgroundTask", "3", "clear_finished");
        // 不传 taskid 时由 DSM 清空所有已完成任务，请求不应携带该参数。
        assertFalse(clearAllRequest.getParameters().containsKey("taskid"));

        fileStation.backgroundTask().clearFinished(Arrays.asList("t1", "t2"));

        SynologyHttpRequest clearSomeRequest = httpClient.getLastRequest();
        assertApi(clearSomeRequest, "SYNO.FileStation.BackgroundTask", "3", "clear_finished");
        assertEquals("[\"t1\",\"t2\"]", clearSomeRequest.getParameters().get("taskid"));
    }

    @Test
    void sharingLifecycleMethods() {
        fileStation.sharing().getInfo("sid-1");

        SynologyHttpRequest getInfoRequest = httpClient.getLastRequest();
        assertApi(getInfoRequest, "SYNO.FileStation.Sharing", "3", "getinfo");
        assertEquals("\"sid-1\"", getInfoRequest.getParameters().get("id"));

        fileStation.sharing().list(SharingListRequest.builder().limit(5).forceClean(true).build());

        SynologyHttpRequest listRequest = httpClient.getLastRequest();
        assertApi(listRequest, "SYNO.FileStation.Sharing", "3", "list");
        assertEquals("5", listRequest.getParameters().get("limit"));
        assertEquals("true", listRequest.getParameters().get("force_clean"));

        fileStation.sharing().create(SharingCreateRequest.builder("/home/a.txt")
                .addPath("/home/b.txt")
                .password("pw")
                .dateExpired("2026-12-31")
                .build());

        SynologyHttpRequest createRequest = httpClient.getLastRequest();
        assertApi(createRequest, "SYNO.FileStation.Sharing", "3", "create");
        assertEquals("[\"/home/a.txt\",\"/home/b.txt\"]", createRequest.getParameters().get("path"));
        assertEquals("\"pw\"", createRequest.getParameters().get("password"));
        assertEquals("\"2026-12-31\"", createRequest.getParameters().get("date_expired"));

        fileStation.sharing().delete(Arrays.asList("id-1", "id-2"));

        SynologyHttpRequest deleteRequest = httpClient.getLastRequest();
        assertApi(deleteRequest, "SYNO.FileStation.Sharing", "3", "delete");
        assertEquals("[\"id-1\",\"id-2\"]", deleteRequest.getParameters().get("id"));

        fileStation.sharing().clearInvalid();

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Sharing", "3", "clear_invalid");

        fileStation.sharing().edit(SharingEditRequest.builder("id-1").password("pw2").build());

        SynologyHttpRequest editRequest = httpClient.getLastRequest();
        assertApi(editRequest, "SYNO.FileStation.Sharing", "3", "edit");
        assertEquals("\"id-1\"", editRequest.getParameters().get("id"));
        assertEquals("\"pw2\"", editRequest.getParameters().get("password"));
    }

    @Test
    void favoriteMethods() {
        fileStation.favorite().list(FavoriteListRequest.builder()
                .limit(5)
                .addAdditional(Additional.OWNER)
                .build());

        SynologyHttpRequest listRequest = httpClient.getLastRequest();
        assertApi(listRequest, "SYNO.FileStation.Favorite", "2", "list");
        assertEquals("5", listRequest.getParameters().get("limit"));
        assertEquals("[\"owner\"]", listRequest.getParameters().get("additional"));

        fileStation.favorite().add(FavoriteAddRequest.builder("/home", "my-home").index(0).build());

        SynologyHttpRequest addRequest = httpClient.getLastRequest();
        assertApi(addRequest, "SYNO.FileStation.Favorite", "2", "add");
        assertEquals("\"/home\"", addRequest.getParameters().get("path"));
        assertEquals("\"my-home\"", addRequest.getParameters().get("name"));
        assertEquals("0", addRequest.getParameters().get("index"));

        fileStation.favorite().delete("/home");

        SynologyHttpRequest deleteRequest = httpClient.getLastRequest();
        assertApi(deleteRequest, "SYNO.FileStation.Favorite", "2", "delete");
        assertEquals("\"/home\"", deleteRequest.getParameters().get("path"));

        fileStation.favorite().clearBroken();

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Favorite", "2", "clear_broken");

        fileStation.favorite().edit(FavoriteEditRequest.builder("/home", "renamed").build());

        SynologyHttpRequest editRequest = httpClient.getLastRequest();
        assertApi(editRequest, "SYNO.FileStation.Favorite", "2", "edit");
        assertEquals("\"renamed\"", editRequest.getParameters().get("name"));

        fileStation.favorite().replaceAll(FavoriteReplaceAllRequest.builder("/home", "my-home")
                .addFavorite("/photo", "my-photo")
                .build());

        SynologyHttpRequest replaceRequest = httpClient.getLastRequest();
        assertApi(replaceRequest, "SYNO.FileStation.Favorite", "2", "replace_all");
        assertEquals("[\"/home\",\"/photo\"]", replaceRequest.getParameters().get("path"));
        assertEquals("[\"my-home\",\"my-photo\"]", replaceRequest.getParameters().get("name"));
    }

    @Test
    void virtualFolderListQuotesType() {
        fileStation.virtualFolder().list(VirtualFolderListRequest.builder("nfs").limit(10).build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertApi(request, "SYNO.FileStation.VirtualFolder", "2", "list");
        assertEquals("\"nfs\"", request.getParameters().get("type"));
        assertEquals("10", request.getParameters().get("limit"));
    }

    @Test
    void compressStartStatusAndStop() {
        fileStation.compress().start(CompressStartRequest.builder("/home/a.txt", "/home/archives/a.zip")
                .addPath("/home/b.txt")
                .level(CompressLevel.BEST)
                .mode(CompressMode.ADD)
                .format(CompressFormat.ZIP)
                .password("pw")
                .build());

        SynologyHttpRequest startRequest = httpClient.getLastRequest();
        assertApi(startRequest, "SYNO.FileStation.Compress", "3", "start");
        assertEquals("[\"/home/a.txt\",\"/home/b.txt\"]", startRequest.getParameters().get("path"));
        assertEquals("\"/home/archives/a.zip\"", startRequest.getParameters().get("dest_file_path"));
        assertEquals("best", startRequest.getParameters().get("level"));
        assertEquals("add", startRequest.getParameters().get("mode"));
        assertEquals("zip", startRequest.getParameters().get("format"));
        assertEquals("pw", startRequest.getParameters().get("password"));

        fileStation.compress().status("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Compress", "3", "status");

        fileStation.compress().stop("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Compress", "3", "stop");
    }

    @Test
    void extractStartListStatusAndStop() {
        fileStation.extract().start(ExtractStartRequest.builder("/home/a.zip", "/home/out")
                .overwrite(true)
                .keepDir(false)
                .createSubfolder(true)
                .addItemId(1)
                .addItemId(2)
                .build());

        SynologyHttpRequest startRequest = httpClient.getLastRequest();
        assertApi(startRequest, "SYNO.FileStation.Extract", "2", "start");
        assertEquals("\"/home/a.zip\"", startRequest.getParameters().get("file_path"));
        assertEquals("\"/home/out\"", startRequest.getParameters().get("dest_folder_path"));
        assertEquals("true", startRequest.getParameters().get("overwrite"));
        assertEquals("false", startRequest.getParameters().get("keep_dir"));
        assertEquals("true", startRequest.getParameters().get("create_subfolder"));
        // item_id 多值按官方要求使用逗号分隔且不加引号。
        assertEquals("1,2", startRequest.getParameters().get("item_id"));

        fileStation.extract().list(ExtractListRequest.builder("/home/a.zip")
                .offset(0)
                .limit(5)
                .itemId(3)
                .build());

        SynologyHttpRequest listRequest = httpClient.getLastRequest();
        assertApi(listRequest, "SYNO.FileStation.Extract", "2", "list");
        assertEquals("\"/home/a.zip\"", listRequest.getParameters().get("file_path"));
        assertEquals("0", listRequest.getParameters().get("offset"));
        assertEquals("5", listRequest.getParameters().get("limit"));
        assertEquals("3", listRequest.getParameters().get("item_id"));

        fileStation.extract().status("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Extract", "2", "status");

        fileStation.extract().stop("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Extract", "2", "stop");
    }

    @Test
    void fileCreateFolderRenamesAndDeletes() {
        fileStation.file().createFolder(CreateFolderRequest.builder()
                .addFolder("/home", "a")
                .addFolder("/home", "b")
                .forceParent(true)
                .addAdditional(Additional.TIME)
                .build());

        SynologyHttpRequest createRequest = httpClient.getLastRequest();
        assertApi(createRequest, "SYNO.FileStation.CreateFolder", "2", "create");
        assertEquals("[\"/home\",\"/home\"]", createRequest.getParameters().get("folder_path"));
        assertEquals("[\"a\",\"b\"]", createRequest.getParameters().get("name"));
        assertEquals("true", createRequest.getParameters().get("force_parent"));
        assertEquals("[\"time\"]", createRequest.getParameters().get("additional"));

        fileStation.file().rename(RenameRequest.builder()
                .addRename("/home/a.txt", "b.txt")
                .searchTaskId("tid")
                .build());

        SynologyHttpRequest renameRequest = httpClient.getLastRequest();
        assertApi(renameRequest, "SYNO.FileStation.Rename", "2", "rename");
        assertEquals("[\"/home/a.txt\"]", renameRequest.getParameters().get("path"));
        assertEquals("[\"b.txt\"]", renameRequest.getParameters().get("name"));
        assertEquals("\"tid\"", renameRequest.getParameters().get("search_task_id"));

        fileStation.file().deleteBlocking(DeleteRequest.builder()
                .addPath("/home/a.txt")
                .recursive(true)
                .build());

        SynologyHttpRequest blockingRequest = httpClient.getLastRequest();
        assertApi(blockingRequest, "SYNO.FileStation.Delete", "2", "delete");
        assertEquals("[\"/home/a.txt\"]", blockingRequest.getParameters().get("path"));
        assertEquals("true", blockingRequest.getParameters().get("recursive"));

        fileStation.file().deleteAsync(DeleteRequest.builder()
                .addPath("/home/a.txt")
                .accurateProgress(true)
                .build());

        SynologyHttpRequest asyncRequest = httpClient.getLastRequest();
        assertApi(asyncRequest, "SYNO.FileStation.Delete", "2", "start");
        assertEquals("true", asyncRequest.getParameters().get("accurate_progress"));

        fileStation.file().deleteStatus("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Delete", "2", "status");

        fileStation.file().deleteStop("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.Delete", "2", "stop");
    }

    @Test
    void fileCopyAndMoveDifferByRemoveSrc() {
        fileStation.file().copy(CopyMoveRequest.builder("/home/copy")
                .addPath("/home/a.txt")
                .overwrite(true)
                .build());

        SynologyHttpRequest copyRequest = httpClient.getLastRequest();
        assertApi(copyRequest, "SYNO.FileStation.CopyMove", "3", "start");
        assertEquals("[\"/home/a.txt\"]", copyRequest.getParameters().get("path"));
        assertEquals("\"/home/copy\"", copyRequest.getParameters().get("dest_folder_path"));
        assertEquals("true", copyRequest.getParameters().get("overwrite"));
        // copy 语义下 remove_src 必须为 false。
        assertEquals("false", copyRequest.getParameters().get("remove_src"));

        fileStation.file().move(CopyMoveRequest.builder("/home/move")
                .addPath("/home/a.txt")
                .build());

        SynologyHttpRequest moveRequest = httpClient.getLastRequest();
        assertApi(moveRequest, "SYNO.FileStation.CopyMove", "3", "start");
        // move 语义下 remove_src 必须为 true。
        assertEquals("true", moveRequest.getParameters().get("remove_src"));

        fileStation.file().copyMoveStatus("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.CopyMove", "3", "status");

        fileStation.file().copyMoveStop("tid");

        assertApi(httpClient.getLastRequest(), "SYNO.FileStation.CopyMove", "3", "stop");
    }

    @Test
    void uploadBuildsMultipartRequestWithDefaultV2() {
        fileStation.upload().file(UploadFileRequest.builder("/home", tempUploadFile)
                .createParents(true)
                .overwrite(false)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertEquals(SynologyHttpMethod.POST, request.getMethod());
        assertEquals("SYNO.FileStation.Upload", request.getParameters().get("api"));
        assertEquals("2", request.getParameters().get("version"));
        assertEquals("upload", request.getParameters().get("method"));
        // 上传的 path 是 multipart 普通表单字段，不使用 JSON 引号包裹。
        assertEquals("/home", request.getParameters().get("path"));
        assertEquals("true", request.getParameters().get("create_parents"));
        assertEquals("false", request.getParameters().get("overwrite"));
        assertNull(request.getParameters().get("mtime"));
        // 官方要求 file part 放在最后一个表单字段。
        assertEquals(1, request.getMultipartParts().size());
        assertEquals("file", request.getMultipartParts().get(0).getName());
    }

    @Test
    void uploadUsesV3WhenOverwritePolicyPresent() {
        fileStation.upload().file(UploadFileRequest.builder("/home", tempUploadFile)
                .overwritePolicy(UploadOverwritePolicy.SKIP)
                .mtime(123L)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertEquals("3", request.getParameters().get("version"));
        // v3 覆盖策略使用字符串枚举值，而非 v2 的布尔值。
        assertEquals("skip", request.getParameters().get("overwrite"));
        assertEquals("123", request.getParameters().get("mtime"));
    }

    @Test
    void downloadRequestsFileAsStream() {
        fileStation.download().file("/home/a.txt");

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertEquals(SynologyHttpMethod.GET, request.getMethod());
        assertEquals(ResponseBodyMode.STREAM, request.getResponseBodyMode());
        assertApi(request, "SYNO.FileStation.Download", "2", "download");
        assertEquals("[\"/home/a.txt\"]", request.getParameters().get("path"));
        assertEquals("\"download\"", request.getParameters().get("mode"));
    }

    @Test
    void thumbRequestsThumbnailAsStream() {
        fileStation.thumb().get(ThumbGetRequest.builder("/home/a.jpg")
                .size(ThumbSize.SMALL)
                .rotate(90)
                .build());

        SynologyHttpRequest request = httpClient.getLastRequest();
        assertEquals(SynologyHttpMethod.GET, request.getMethod());
        assertEquals(ResponseBodyMode.STREAM, request.getResponseBodyMode());
        assertApi(request, "SYNO.FileStation.Thumb", "2", "get");
        assertEquals("\"/home/a.jpg\"", request.getParameters().get("path"));
        assertEquals("small", request.getParameters().get("size"));
        assertEquals("90", request.getParameters().get("rotate"));
    }

    /**
     * 断言请求的基础参数：URL、api、version、method 以及认证 SID。
     */
    private void assertApi(SynologyHttpRequest request, String expectedApi, String expectedVersion, String expectedMethod) {
        assertEquals("http://nas:5000/webapi/entry.cgi", request.getUrl());
        assertEquals(expectedApi, request.getParameters().get("api"));
        assertEquals(expectedVersion, request.getParameters().get("version"));
        assertEquals(expectedMethod, request.getParameters().get("method"));
        assertEquals(SID, request.getParameters().get("_sid"));
    }

    /**
     * 记录全部请求并固定返回 success 响应的 HTTP fake。
     * <p>
     * 契约测试只断言请求内容，响应统一返回 success；流式请求由执行器直接透传原始响应。
     */
    private static class RecordingSynologyHttpClient implements SynologyHttpClient {

        private final List<SynologyHttpRequest> requests = new ArrayList<SynologyHttpRequest>();

        @Override
        public SynologyHttpResponse execute(SynologyHttpRequest request) {
            requests.add(request);
            return new SynologyHttpResponse(200, null, "{\"success\":true}", new ByteArrayInputStream(new byte[0]));
        }

        SynologyHttpRequest getLastRequest() {
            return requests.get(requests.size() - 1);
        }
    }

    /**
     * 返回固定 SID 的会话管理器，避免契约测试触发真实登录流程。
     */
    private static class FixedSidSessionManager extends SynologySessionManager {

        private final SynologySession session;

        FixedSidSessionManager(SynologyDsmConfig config) {
            super(config, (AuthClient) null);
            this.session = new SynologySession(SID, config.getSessionName(), null);
        }

        @Override
        public synchronized SynologySession currentSession() {
            return session;
        }
    }

    /**
     * 契约测试用最小 JSON fake：统一返回 success 且 data 为空。
     */
    private static class SuccessJsonCodec implements SynologyJsonCodec {

        @Override
        public <T> SynologyJsonResponse<T> decode(String body, Class<T> dataType) {
            return new SynologyJsonResponse<T>(Boolean.TRUE, null, null);
        }

        @Override
        public <T> SynologyJsonResponse<Map<String, T>> decodeMap(String body, Class<T> valueType) {
            return new SynologyJsonResponse<Map<String, T>>(Boolean.TRUE, null, null);
        }
    }
}
