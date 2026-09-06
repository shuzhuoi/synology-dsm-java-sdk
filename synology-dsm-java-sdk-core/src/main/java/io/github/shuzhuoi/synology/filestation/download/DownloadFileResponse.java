package io.github.shuzhuoi.synology.filestation.download;

import lombok.Getter;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SYNO.FileStation.Download 的下载响应。
 * <p>
 * {@code inputStream} 是底层 HTTP 连接的原始流，使用完毕必须由调用方关闭，
 * 否则连接无法归还连接池，高频下载时可能耗尽连接。推荐使用 try-with-resources。
 */
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
