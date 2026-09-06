package io.github.shuzhuoi.synology.filestation.task;

import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖 {@link SynologyTaskPoller} 的轮询行为：
 * 首次即完成直接返回、多次未完成后成功、超时抛异常并按配置执行 stop、入参校验和 null 状态容错。
 * 轮询间隔统一设为 0，避免测试真实休眠。
 */
class SynologyTaskPollerTest {

    @Test
    void returnsImmediatelyWhenFirstStatusIsFinished() {
        AtomicInteger queryCount = new AtomicInteger();
        TaskPollingOptions options = fastOptions(60);

        String result = SynologyTaskPoller.wait(
                () -> "finished-" + queryCount.incrementAndGet(),
                "finished-"::equals,
                options
        );

        assertEquals("finished-1", result);
        assertEquals(1, queryCount.get());
    }

    @Test
    void pollsMultipleTimesWhenStatusStartsUnfinished() {
        TaskPollingOptions options = fastOptions(60);

        String result = SynologyTaskPoller.wait(
                new SequencedStatusSupplier("running", "running", "finished"),
                "finished"::equals,
                options
        );

        assertEquals("finished", result);
    }

    @Test
    void throwsDsmExceptionAfterMaxAttemptsWhenNeverFinished() {
        AtomicInteger queryCount = new AtomicInteger();
        TaskPollingOptions options = fastOptions(3);

        SynologyDsmException exception = assertThrows(SynologyDsmException.class, () ->
                SynologyTaskPoller.wait(
                        () -> "running-" + queryCount.incrementAndGet(),
                        "finished"::equals,
                        options
                )
        );

        // 达到最大尝试次数仍未完成时抛异常，异常信息包含尝试次数便于排查。
        assertEquals(3, queryCount.get());
        assertTrue(exception.getMessage().contains("3 attempts"));
    }

    @Test
    void runsStopActionOnTimeoutWhenStopOnTimeoutEnabled() {
        AtomicInteger stopCount = new AtomicInteger();
        TaskPollingOptions options = TaskPollingOptions.builder()
                .intervalMillis(0L)
                .maxAttempts(2)
                .stopOnTimeout(true)
                .build();

        assertThrows(SynologyDsmException.class, () ->
                SynologyTaskPoller.wait(
                        () -> "running",
                        "finished"::equals,
                        stopCount::incrementAndGet,
                        options
                )
        );

        assertEquals(1, stopCount.get());
    }

    @Test
    void skipsStopActionWhenStopOnTimeoutDisabled() {
        AtomicInteger stopCount = new AtomicInteger();
        TaskPollingOptions options = fastOptions(2);

        assertThrows(SynologyDsmException.class, () ->
                SynologyTaskPoller.wait(
                        () -> "running",
                        "finished"::equals,
                        stopCount::incrementAndGet,
                        options
                )
        );

        // 默认 stopOnTimeout=false，超时不应执行 stop，由调用方决定是否清理。
        assertEquals(0, stopCount.get());
    }

    @Test
    void toleratesNullTimeoutStopAction() {
        TaskPollingOptions options = TaskPollingOptions.builder()
                .intervalMillis(0L)
                .maxAttempts(1)
                .stopOnTimeout(true)
                .build();

        // stopOnTimeout=true 但未提供 stop 动作时不应抛 NPE。
        assertThrows(SynologyDsmException.class, () ->
                SynologyTaskPoller.wait(() -> "running", "finished"::equals, null, options)
        );
    }

    @Test
    void treatsNullStatusAsUnfinishedAndKeepsPolling() {
        TaskPollingOptions options = fastOptions(60);

        String result = SynologyTaskPoller.wait(
                new SequencedStatusSupplier(null, "finished"),
                "finished"::equals,
                options
        );

        // status 查询返回 null（例如解析失败兜底）时按未完成处理，不抛 NPE。
        assertEquals("finished", result);
    }

    @Test
    void rejectsNullArguments() {
        TaskPollingOptions options = fastOptions(1);

        assertThrows(IllegalArgumentException.class, () ->
                SynologyTaskPoller.wait(null, "finished"::equals, options));
        assertThrows(IllegalArgumentException.class, () ->
                SynologyTaskPoller.wait(() -> "finished", null, options));
        assertThrows(IllegalArgumentException.class, () ->
                SynologyTaskPoller.wait(() -> "finished", "finished"::equals, null));
    }

    /**
     * 构造不真实休眠的轮询配置。
     */
    private TaskPollingOptions fastOptions(int maxAttempts) {
        return TaskPollingOptions.builder()
                .intervalMillis(0L)
                .maxAttempts(maxAttempts)
                .build();
    }

    /**
     * 按顺序返回预设状态的 Supplier，用于模拟任务先进行后完成的场景。
     */
    private static class SequencedStatusSupplier implements java.util.function.Supplier<String> {

        private final String[] statuses;
        private int index;

        SequencedStatusSupplier(String... statuses) {
            this.statuses = statuses;
        }

        @Override
        public String get() {
            String status = statuses[Math.min(index, statuses.length - 1)];
            index++;
            return status;
        }
    }
}
