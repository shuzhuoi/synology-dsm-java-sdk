package io.github.shuzhuoi.synology.filestation.info;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

import java.util.Collections;

/**
 * SYNO.FileStation.Info 客户端。
 */
public class FileStationInfoClient {

    private final SynologyApiExecutor executor;

    public FileStationInfoClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public FileStationInfoResponse get() {
        // 官方 get 方法无业务参数，只需要登录后的 SID。
        return executor.getAuthenticated(
                "entry.cgi",
                "SYNO.FileStation.Info",
                2,
                "get",
                Collections.<String, String>emptyMap(),
                FileStationInfoResponse.class
        );
    }
}
