package io.github.shuzhuoi.synology.auth;

import java.util.Date;


public class SynologySession {

    private final String sid;
    private final String sessionName;
    private final Date createdAt;

    public SynologySession(String sid, String sessionName, Date createdAt) {
        this.sid = sid;
        this.sessionName = sessionName;
        this.createdAt = createdAt == null ? new Date() : new Date(createdAt.getTime());
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
}
