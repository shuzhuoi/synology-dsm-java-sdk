package io.github.shuzhuoi.synology.downloadstation.info;

import io.github.shuzhuoi.synology.downloadstation.DownloadStationApi;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.DownloadStation2.Info 客户端。
 * <p>
 * 提供 Download Station 的版本信息和全局配置读写。
 */
public class DownloadStationInfoClient {

    private final SynologyApiExecutor executor;

    public DownloadStationInfoClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public DownloadStationInfoResponse getInfo() {
        // getInfo 无业务参数，返回版本与管理员标识。
        return executor.getAuthenticated(
                "entry.cgi",
                DownloadStationApi.INFO_API,
                DownloadStationApi.INFO_VERSION,
                "getInfo",
                Collections.<String, String>emptyMap(),
                DownloadStationInfoResponse.class
        );
    }

    public DownloadStationConfigResponse getConfig() {
        // getConfig 无业务参数，返回各协议限速与默认目的地等全局配置。
        return executor.getAuthenticated(
                "entry.cgi",
                DownloadStationApi.INFO_API,
                DownloadStationApi.INFO_VERSION,
                "getConfig",
                Collections.<String, String>emptyMap(),
                DownloadStationConfigResponse.class
        );
    }

    /**
     * 修改 Download Station 全局配置。
     * <p>
     * 请求对象中只有显式设置的字段才会下发，未设置的字段保持 DSM 当前值。
     *
     * @param request 配置修改请求
     * @return 操作结果
     */
    public SynologyOperationResponse setServerConfig(DownloadStationConfigRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("bt_max_download", SynologyParameterEncoder.integerValue(request.getBtMaxDownload()));
        parameters.put("bt_max_upload", SynologyParameterEncoder.integerValue(request.getBtMaxUpload()));
        parameters.put("emule_max_download", SynologyParameterEncoder.integerValue(request.getEmuleMaxDownload()));
        parameters.put("emule_max_upload", SynologyParameterEncoder.integerValue(request.getEmuleMaxUpload()));
        parameters.put("nzb_max_download", SynologyParameterEncoder.integerValue(request.getNzbMaxDownload()));
        parameters.put("http_max_download", SynologyParameterEncoder.integerValue(request.getHttpMaxDownload()));
        parameters.put("ftp_max_download", SynologyParameterEncoder.integerValue(request.getFtpMaxDownload()));
        parameters.put("emule_enabled", SynologyParameterEncoder.booleanValue(request.getEmuleEnabled()));
        parameters.put("unzip_service_enabled", SynologyParameterEncoder.booleanValue(request.getUnzipServiceEnabled()));
        // 目的地路径官方要求以 JSON 字符串形式传输，即带引号。
        parameters.put("default_destination", SynologyParameterEncoder.quoted(request.getDefaultDestination()));
        parameters.put("emule_default_destination", SynologyParameterEncoder.quoted(request.getEmuleDefaultDestination()));
        executor.getAuthenticated(
                "entry.cgi",
                DownloadStationApi.INFO_API,
                DownloadStationApi.INFO_VERSION,
                "setServerConfig",
                parameters,
                Object.class
        );
        // 失败时执行器已抛出 SynologyApiException，能走到这里即成功。
        return new SynologyOperationResponse(true);
    }
}
