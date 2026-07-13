package io.github.shuzhuoi.synology.filestation.upload;

import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.internal.request.SynologyApiRequest;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

/**
 * SYNO.FileStation.Upload 客户端。
 */
public class FileStationUploadClient {

    private final SynologyApiExecutor executor;

    public FileStationUploadClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public UploadFileResponse file(UploadFileRequest request) {
        // 上传接口使用 multipart/form-data，普通参数和文件内容都放在表单 part 中。
        SynologyApiRequest apiRequest = SynologyApiRequest.builder()
                .path("entry.cgi")
                .apiName("SYNO.FileStation.Upload")
                .version(request.useUploadApiV3() ? 3 : 2)
                .method("upload")
                .authenticated(true)
                .parameter("path", request.getPath())
                .parameter("create_parents", SynologyParameterEncoder.booleanValue(request.getCreateParents()))
                .parameter("overwrite", request.overwriteParameter())
                .parameter("mtime", SynologyParameterEncoder.longValue(request.getMtime()))
                .parameter("crtime", SynologyParameterEncoder.longValue(request.getCrtime()))
                .parameter("atime", SynologyParameterEncoder.longValue(request.getAtime()))
                .responseType(Object.class)
                .httpMethod(SynologyHttpMethod.POST)
                // 官方文档要求 multipart 上传时 file 字段放在最后。
                .multipartPart(SynologyMultipartPart.file("file", request.getFile()))
                .build();
        executor.executeAuthenticated(apiRequest);
        return new UploadFileResponse(true);
    }
}
