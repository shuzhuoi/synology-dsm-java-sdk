package io.github.shuzhuoi.synology.filestation.upload;

import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
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
        SynologyHttpRequest multipartRequest = SynologyHttpRequest.builder()
                .parameter("path", request.getPath())
                .parameter("create_parents", SynologyParameterEncoder.booleanValue(request.getCreateParents()))
                .parameter("overwrite", SynologyParameterEncoder.booleanValue(request.getOverwrite()))
                // 官方文档要求 multipart 上传时 file 字段放在最后。
                .multipartPart(SynologyMultipartPart.file("file", request.getFile()))
                .build();
        executor.executeMultipartAuthenticated(
                "entry.cgi",
                "SYNO.FileStation.Upload",
                2,
                "upload",
                multipartRequest,
                Object.class
        );
        return new UploadFileResponse(true);
    }
}
