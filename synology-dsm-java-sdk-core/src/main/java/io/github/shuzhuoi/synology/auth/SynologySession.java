package io.github.shuzhuoi.synology.auth;

import java.util.Date;


public class SynologySession {

    private final String sid;
    private final String sessionName;
    private final Date createdAt;
    /**
     * 设备 ID（did）。登录时开启 enableDeviceToken 才有值，
     * 持久化后可在下次登录时作为 deviceId 免两步验证。
     */
    private final String deviceId;

    public SynologySession(String sid, String sessionName, Date createdAt) {
        this(sid, sessionName, createdAt, null);
    }

    public SynologySession(String sid, String sessionName, Date createdAt, String deviceId) {
        this.sid = sid;
        this.sessionName = sessionName;
        this.createdAt = createdAt == null ? new Date() : new Date(createdAt.getTime());
        this.deviceId = deviceId;
    }

    public String getSid() {
        return sid;
    }

    public String getSessionName() {
        return sessionName;
    }

    public Date getCreatedAt() {
        return new Date(createdAt.getTime());
    }

    public String getDeviceId() {
        return deviceId;
    }
}
