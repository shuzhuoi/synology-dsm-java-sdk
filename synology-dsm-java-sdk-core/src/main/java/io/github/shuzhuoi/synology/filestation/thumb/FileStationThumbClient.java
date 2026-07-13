package io.github.shuzhuoi.synology.filestation.thumb;

import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.internal.request.SynologyApiRequest;
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
        SynologyApiRequest apiRequest = SynologyApiRequest.builder()
                .path("entry.cgi")
                .apiName(API_NAME)
                .version(API_VERSION)
                .method("get")
                .authenticated(true)
                .parameters(parameters)
                .responseType(SynologyHttpResponse.class)
                .responseBodyMode(ResponseBodyMode.STREAM)
                .build();
        SynologyHttpResponse response = executor.executeAuthenticated(apiRequest);
        return new ThumbGetResponse(response.getStatusCode(), response.getHeaders(), response.getBodyStream());
    }
}
