package io.github.shuzhuoi.synology.filestation.download;

import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.Download 客户端。
 */
public class FileStationDownloadClient {

    private final SynologyApiExecutor executor;

    public FileStationDownloadClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public DownloadFileResponse file(String path) {
        return file(DownloadFileRequest.builder().addPath(path).build());
    }

    public DownloadFileResponse file(DownloadFileRequest request) {
        // 单文件下载返回原始文件流；多文件或文件夹下载时 DSM 会返回 zip 内容。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        parameters.put("mode", SynologyParameterEncoder.quoted(request.getMode().getValue()));
        SynologyHttpResponse response = executor.downloadAuthenticated(
                "entry.cgi",
                "SYNO.FileStation.Download",
                2,
                "download",
                parameters
        );
        return new DownloadFileResponse(response.getStatusCode(), response.getHeaders(), response.getBodyStream());
    }
}
