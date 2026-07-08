package io.github.shuzhuoi.synology.config;

/**
 * DSM 连接配置。账号、密码、SID 等敏感信息禁止直接输出到日志。
 */
public class SynologyDsmConfig {

    /**
     * DSM 基础访问地址，不包含 /webapi，例如 http://192.168.1.10:5000。
     */
    private final String baseUrl;
    /**
     * DSM 登录账号。
     */
    private final String account;
    /**
     * DSM 登录密码。注意：不要在日志中输出该字段。
     */
    private final String password;
    /**
     * DSM WebAPI 会话名。File Station 固定使用 FileStation。
     */
    private final String sessionName;
    /**
     * HTTP 连接超时时间，单位毫秒。
     */
    private final int connectTimeoutMillis;
    /**
     * HTTP 读取超时时间，单位毫秒。上传/下载大文件时可适当调大。
     */
    private final int readTimeoutMillis;
    /**
     * 没有 SID 时是否自动登录。
     */
    private final boolean autoLogin;
    /**
     * 会话失效时是否允许后续扩展为自动刷新 SID。
     */
    private final boolean autoRefreshSession;

    private SynologyDsmConfig(Builder builder) {
        this.baseUrl = normalizeBaseUrl(builder.baseUrl);
        this.account = builder.account;
        this.password = builder.password;
        this.sessionName = builder.sessionName;
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.autoLogin = builder.autoLogin;
        this.autoRefreshSession = builder.autoRefreshSession;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getSessionName() {
        return sessionName;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public boolean isAutoRefreshSession() {
        return autoRefreshSession;
    }

    public String resolveWebApiUrl(String apiPath) {
        String path = apiPath == null || apiPath.length() == 0 ? "entry.cgi" : apiPath;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.startsWith("webapi/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + "/webapi/" + path;
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().length() == 0) {
            throw new IllegalArgumentException("baseUrl must not be blank");
        }
        String normalized = baseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static class Builder {

        /**
         * DSM 基础地址，例如 http://192.168.1.10:5000 或 https://nas.example.com:5001。
         */
        private String baseUrl;
        private String account;
        private String password;
        private String sessionName = "FileStation";
        private int connectTimeoutMillis = 10000;
        private int readTimeoutMillis = 60000;
        private boolean autoLogin = true;
        private boolean autoRefreshSession = true;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder account(String account) {
            this.account = account;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder sessionName(String sessionName) {
            this.sessionName = sessionName;
            return this;
        }

        public Builder connectTimeoutMillis(int connectTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        public Builder readTimeoutMillis(int readTimeoutMillis) {
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }

        public Builder autoLogin(boolean autoLogin) {
            this.autoLogin = autoLogin;
            return this;
        }

        public Builder autoRefreshSession(boolean autoRefreshSession) {
            this.autoRefreshSession = autoRefreshSession;
            return this;
        }

        public SynologyDsmConfig build() {
            return new SynologyDsmConfig(this);
        }
    }
}
