package io.github.shuzhuoi.synology.spring.boot2;

import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot 2 的 DSM 连接配置。
 *
 * <p>密码只用于构造 core 配置，不提供日志输出或 {@code toString} 实现。</p>
 */
@ConfigurationProperties(prefix = "synology.dsm")
public class SynologyDsmProperties {

    private boolean enabled = true;
    private String baseUrl;
    private String account;
    private String password;
    private String sessionName = "FileStation";
    private int connectTimeoutMillis = 10000;
    private int readTimeoutMillis = 60000;
    private boolean autoLogin = true;
    private boolean autoRefreshSession = true;
    private String httpAdapter = "hutool";

    public SynologyDsmConfig toConfig() {
        return SynologyDsmConfig.builder()
                .baseUrl(baseUrl)
                .account(account)
                .password(password)
                .sessionName(sessionName)
                .connectTimeoutMillis(connectTimeoutMillis)
                .readTimeoutMillis(readTimeoutMillis)
                .autoLogin(autoLogin)
                .autoRefreshSession(autoRefreshSession)
                .build();
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    public int getConnectTimeoutMillis() { return connectTimeoutMillis; }
    public void setConnectTimeoutMillis(int value) { this.connectTimeoutMillis = value; }
    public int getReadTimeoutMillis() { return readTimeoutMillis; }
    public void setReadTimeoutMillis(int value) { this.readTimeoutMillis = value; }
    public boolean isAutoLogin() { return autoLogin; }
    public void setAutoLogin(boolean autoLogin) { this.autoLogin = autoLogin; }
    public boolean isAutoRefreshSession() { return autoRefreshSession; }
    public void setAutoRefreshSession(boolean value) { this.autoRefreshSession = value; }
    public String getHttpAdapter() { return httpAdapter; }
    public void setHttpAdapter(String httpAdapter) { this.httpAdapter = httpAdapter; }
}
