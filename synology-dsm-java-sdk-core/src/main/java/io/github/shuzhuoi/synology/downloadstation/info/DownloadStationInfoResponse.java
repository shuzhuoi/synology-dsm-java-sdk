package io.github.shuzhuoi.synology.downloadstation.info;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SYNO.DownloadStation2.Info getInfo 方法的响应。
 */
@Getter
@Setter
@NoArgsConstructor
public class DownloadStationInfoResponse {

    /**
     * 当前登录用户是否为 Download Station 管理员。
     */
    @SynologyJsonProperty("is_manager")
    private Boolean manager;
    /**
     * Download Station 版本号，例如 3.8.16-3566。
     */
    private String version;
    /**
     * 版本号的可读字符串形式。
     */
    @SynologyJsonProperty("version_string")
    private String versionString;
}
