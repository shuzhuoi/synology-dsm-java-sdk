package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.exception.SynologyApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖会话失效错误码和 retry-once 判断，避免错误码散落回执行器。
 */
class SynologySessionRetryPolicyTest {

    @Test
    void retriesKnownSessionErrorsWhenAutoRefreshEnabled() {
        SynologySessionRetryPolicy policy = new SynologySessionRetryPolicy(true);

        assertTrue(policy.shouldRefreshAndRetry(apiException(106), 0));
        assertTrue(policy.shouldRefreshAndRetry(apiException(107), 0));
        assertTrue(policy.shouldRefreshAndRetry(apiException(119), 0));
    }

    @Test
    void doesNotRetryWhenDisabledAlreadyRetriedOrNotSessionError() {
        assertFalse(new SynologySessionRetryPolicy(false).shouldRefreshAndRetry(apiException(106), 0));

        SynologySessionRetryPolicy policy = new SynologySessionRetryPolicy(true);
        assertFalse(policy.shouldRefreshAndRetry(apiException(106), 1));
        assertFalse(policy.shouldRefreshAndRetry(apiException(400), 0));
        assertFalse(policy.shouldRefreshAndRetry(apiException(null), 0));
        assertFalse(policy.shouldRefreshAndRetry(null, 0));
    }

    private SynologyApiException apiException(Integer code) {
        return new SynologyApiException("SYNO.Test", "list", code, "failed");
    }
}
