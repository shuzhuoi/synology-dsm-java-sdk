package io.github.shuzhuoi.synology.filestation.sharing;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DSM 分享链接信息。
 */
@Getter
@Setter
@NoArgsConstructor
public class SharingLink {

    /**
     * 分享链接 ID。
     */
    private String id;
    /**
     * 分享访问 URL。
     */
    private String url;
    /**
     * 分享者账号。
     */
    @SynologyJsonProperty("link_owner")
    private String linkOwner;
    /**
     * 被分享的文件或目录路径。
     */
    private String path;
    /**
     * 被分享对象名称。
     */
    private String name;
    /**
     * 是否为目录。DSM 字段名为 isFolder。
     */
    @SynologyJsonProperty("isFolder")
    private Boolean folder;
    /**
     * 是否设置访问密码。
     */
    @SynologyJsonProperty("has_password")
    private Boolean hasPassword;
    /**
     * 过期日期，0 表示永久。
     */
    @SynologyJsonProperty("date_expired")
    private String dateExpired;
    /**
     * 生效日期，0 表示立即生效。
     */
    @SynologyJsonProperty("date_available")
    private String dateAvailable;
    /**
     * 分享状态：valid、invalid、expired、broken。
     */
    private String status;
    /**
     * 创建分享时返回的二维码 Base64 字符串。
     */
    private String qrcode;
    /**
     * 创建分享时的文件级错误码，0 表示成功。
     */
    private Integer error;
}
