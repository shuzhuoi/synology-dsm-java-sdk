package io.github.shuzhuoi.synology.filestation.task;

import io.github.shuzhuoi.synology.exception.SynologyDsmException;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Synology 任务型接口的通用轮询器。
 * <p>
 * 官方 Search、DirSize、MD5、Delete、CopyMove 等接口均为非阻塞 API：
 * start 返回 taskid，调用方需要持续查询 status/list 直到 finished 为 true。
 * 这里抽出统一的轮询循环，避免每个任务客户端重复写等待逻辑。
 * <p>
 * 采用函数式设计：调用方传入状态查询函数和完成判断谓词，不要求 Response 类实现统一接口，
 * 从而避免侵入现有 Delete/CopyMove/MD5 等状态响应类。
 */
public final class SynologyTaskPoller {

    private SynologyTaskPoller() {
    }

    /**
     * 轮询直到任务完成或超过最大尝试次数。
     *
     * @param statusSupplier    状态查询函数，每次调用应返回最新的状态响应
     * @param finishedPredicate 完成判断谓词，返回 true 时结束轮询
     * @param options           轮询配置
     * @param <S>               状态响应类型
     * @return 最后一次查询到的状态响应（已完成）
     */
    public static <S> S wait(Supplier<S> statusSupplier, Predicate<S> finishedPredicate, TaskPollingOptions options) {
        return wait(statusSupplier, finishedPredicate, null, options);
    }

    /**
     * 轮询直到任务完成或超过最大尝试次数，并在配置允许时执行超时清理动作。
     *
     * @param statusSupplier    状态查询函数，每次调用应返回最新的状态响应
     * @param finishedPredicate 完成判断谓词，返回 true 时结束轮询
     * @param timeoutStopAction 超时后的 stop 动作；当 stopOnTimeout=false 时不会执行
     * @param options           轮询配置
     * @param <S>               状态响应类型
     * @return 最后一次查询到的状态响应（已完成）
     */
    public static <S> S wait(Supplier<S> statusSupplier, Predicate<S> finishedPredicate,
                             Runnable timeoutStopAction, TaskPollingOptions options) {
        if (statusSupplier == null) {
            throw new IllegalArgumentException("statusSupplier must not be null");
        }
        if (finishedPredicate == null) {
            throw new IllegalArgumentException("finishedPredicate must not be null");
        }
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }

        S status = null;
        for (int attempt = 0; attempt < options.getMaxAttempts(); attempt++) {
            status = statusSupplier.get();
            if (status != null && finishedPredicate.test(status)) {
                return status;
            }
            sleepBeforeNext(options.getIntervalMillis());
        }
        stopIfNecessary(timeoutStopAction, options);
        throw new SynologyDsmException("task polling timeout after " + options.getMaxAttempts()
                + " attempts, please use the corresponding status method for long-running tasks");
    }

    private static void stopIfNecessary(Runnable timeoutStopAction, TaskPollingOptions options) {
        if (!options.isStopOnTimeout() || timeoutStopAction == null) {
            return;
        }
        timeoutStopAction.run();
    }

    private static void sleepBeforeNext(long intervalMillis) {
        try {
            Thread.sleep(intervalMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SynologyDsmException("interrupted while polling task status", e);
        }
    }
}
