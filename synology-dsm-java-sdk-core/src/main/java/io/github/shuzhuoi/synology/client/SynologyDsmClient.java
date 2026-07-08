package io.github.shuzhuoi.synology.client;

import io.github.shuzhuoi.synology.api.ApiInfoClient;
import io.github.shuzhuoi.synology.auth.AuthClient;
import io.github.shuzhuoi.synology.auth.SynologySessionManager;
import io.github.shuzhuoi.synology.config.SynologyDsmConfig;
import io.github.shuzhuoi.synology.filestation.FileStationClient;
import io.github.shuzhuoi.synology.http.SynologyHttpClient;
import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;

/**
 * Synology DSM SDK 的统一入口。
 * <p>
 * 调用方通常只需要持有这个对象，再通过 {@link #apiInfo()}、{@link #auth()}、
 * {@link #session()}、{@link #fileStation()} 进入具体能力模块。
 */
public class SynologyDsmClient {

    /**
     * DSM 连接配置，包含地址、账号、密码、超时和会话策略。
     */
    private final SynologyDsmConfig config;

    /**
     * 统一 API 执行器，负责拼接通用参数、注入 SID、解析响应和转换异常。
     */
    private final SynologyApiExecutor executor;

    /**
     * SYNO.API.Info 客户端，用于查询当前 DSM 支持的 API 信息。
     */
    private final ApiInfoClient apiInfoClient;

    /**
     * SYNO.API.Auth 客户端，用于显式登录和登出。
     */
    private final AuthClient authClient;

    /**
     * 会话管理器，用于自动登录、复用 SID 和登出。
     */
    private final SynologySessionManager sessionManager;

    /**
     * File Station 聚合客户端。
     */
    private final FileStationClient fileStationClient;

    private SynologyDsmClient(Builder builder) {
        this.config = builder.config;
        this.executor = new SynologyApiExecutor(config, builder.httpClient);
        this.authClient = new AuthClient(config, executor);
        this.sessionManager = new SynologySessionManager(config, authClient);
        this.executor.setSessionManager(sessionManager);
        // 根据配置开启会话失效自动重试，避免长连接场景下 SID 过期导致业务中断。
        this.executor.setAutoRefreshSession(config.isAutoRefreshSession());
        this.apiInfoClient = new ApiInfoClient(executor);
        this.fileStationClient = new FileStationClient(executor);
    }

    public static Builder builder() {
        return new Builder();
    }

    public SynologyDsmConfig config() {
        return config;
    }

    public ApiInfoClient apiInfo() {
        return apiInfoClient;
    }

    public AuthClient auth() {
        return authClient;
    }

    public SynologySessionManager session() {
        return sessionManager;
    }

    public FileStationClient fileStation() {
        return fileStationClient;
    }

    public static class Builder {
        private SynologyDsmConfig config;
        private SynologyHttpClient httpClient;

        /**
         * 设置 DSM 连接配置。
         */
        public Builder config(SynologyDsmConfig config) {
            this.config = config;
            return this;
        }

        /**
         * 设置 HTTP 实现。core 模块只依赖抽象，默认 Hutool 实现在 http-hutool 模块中。
         */
        public Builder httpClient(SynologyHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public SynologyDsmClient build() {
            if (config == null) {
                throw new IllegalArgumentException("config must not be null");
            }
            if (httpClient == null) {
                throw new IllegalArgumentException("httpClient must not be null");
            }
            return new SynologyDsmClient(this);
        }
    }
}
