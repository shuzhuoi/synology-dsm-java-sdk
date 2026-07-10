package io.github.shuzhuoi.synology.filestation.thumb;

import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.Thumb 客户端。
 */
public class FileStationThumbClient {

    private static final String API_NAME = "SYNO.FileStation.Thumb";
    private static final int API_VERSION = 2;

    private final SynologyApiExecutor executor;

    public FileStationThumbClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public ThumbGetResponse get(String path) {
        return get(ThumbGetRequest.builder(path).build());
    }

    public ThumbGetResponse get(ThumbGetRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.quoted(request.getPath()));
        parameters.put("size", request.getSize());
        parameters.put("rotate", SynologyParameterEncoder.integerValue(request.getRotate()));
        SynologyHttpResponse response = executor.downloadAuthenticated(
                "entry.cgi",
                API_NAME,
                API_VERSION,
                "get",
                parameters
        );
        return new ThumbGetResponse(response.getStatusCode(), response.getHeaders(), response.getBodyStream());
    }
}
