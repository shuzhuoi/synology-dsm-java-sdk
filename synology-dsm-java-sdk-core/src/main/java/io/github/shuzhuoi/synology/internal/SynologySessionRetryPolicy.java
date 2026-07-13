package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.exception.SynologyApiException;

/**
 * DSM Session 失效后的刷新重试策略。
 * <p>
 * executor 只负责按策略编排重试，不再直接关心哪些错误码代表 SID 失效。
 */
public class SynologySessionRetryPolicy {

    private static final int SESSION_TIMEOUT = 106;
    private static final int SESSION_INTERRUPTED = 107;
    private static final int SID_NOT_FOUND = 119;

    private final boolean autoRefreshSession;

    public SynologySessionRetryPolicy(boolean autoRefreshSession) {
        this.autoRefreshSession = autoRefreshSession;
    }

    /**
     * 判断当前异常是否允许刷新 SID 并重试。
     *
     * @param exception 已解析出的 DSM API 异常
     * @param retryCount 当前请求已经重试的次数
     * @return true 表示可以刷新 session 并再执行一次
     */
    public boolean shouldRefreshAndRetry(SynologyApiException exception, int retryCount) {
        if (!autoRefreshSession || retryCount >= 1 || exception == null) {
            return false;
        }
        return isSessionError(exception.getErrorCode());
    }

    /**
     * DSM 返回这些错误码时表示当前 SID 已不可继续使用。
     */
    public boolean isSessionError(Integer code) {
        if (code == null) {
            return false;
        }
        return code == SESSION_TIMEOUT || code == SESSION_INTERRUPTED || code == SID_NOT_FOUND;
    }
}
