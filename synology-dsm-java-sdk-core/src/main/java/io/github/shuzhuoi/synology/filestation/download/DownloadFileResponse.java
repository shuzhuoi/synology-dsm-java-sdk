package io.github.shuzhuoi.synology.filestation.download;

import lombok.Getter;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class DownloadFileResponse {

    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final InputStream inputStream;

    public DownloadFileResponse(int statusCode, Map<String, List<String>> headers, InputStream inputStream) {
        this.statusCode = statusCode;
        this.headers = headers == null ? Collections.<String, List<String>>emptyMap() : headers;
        this.inputStream = inputStream;
    }
}
