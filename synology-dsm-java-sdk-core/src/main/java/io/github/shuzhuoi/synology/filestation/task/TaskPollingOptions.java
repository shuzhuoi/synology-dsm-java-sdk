package io.github.shuzhuoi.synology.filestation.task;

/**
 * 任务轮询配置。
 * <p>
 * 用于控制 {@link SynologyTaskPoller} 的轮询节奏、最大尝试次数和超时行为。
 * 官方 Search、DirSize、MD5、Delete、CopyMove 等任务型接口均为非阻塞 API，
 * 调用方需要自行轮询 status/list 方法直到 finished 为 true。
 */
public class TaskPollingOptions {

    /**
     * 默认配置：间隔 1 秒，最多 60 次（约 1 分钟），超时不自动 stop。
     */
    public static final TaskPollingOptions DEFAULT = builder().build();

    /**
     * 轮询间隔，单位毫秒。
     */
    private final long intervalMillis;
    /**
     * 最大尝试次数（包含首次查询）。达到上限仍未完成则抛异常。
     */
    private final int maxAttempts;
    /**
     * 超时是否自动调用 stop 取消任务。默认 false，由调用方决定是否清理。
     */
    private final boolean stopOnTimeout;

    private TaskPollingOptions(Builder builder) {
        this.intervalMillis = builder.intervalMillis;
        this.maxAttempts = builder.maxAttempts;
        this.stopOnTimeout = builder.stopOnTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public boolean isStopOnTimeout() {
        return stopOnTimeout;
    }

    public static class Builder {

        private long intervalMillis = 1000L;
        private int maxAttempts = 60;
        private boolean stopOnTimeout = false;

        /**
         * 轮询间隔，单位毫秒。默认 1000。
         */
        public Builder intervalMillis(long intervalMillis) {
            this.intervalMillis = intervalMillis;
            return this;
        }

        /**
         * 最大尝试次数（包含首次查询）。默认 60。
         */
        public Builder maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        /**
         * 超时是否自动调用 stop 取消任务。默认 false。
         */
        public Builder stopOnTimeout(boolean stopOnTimeout) {
            this.stopOnTimeout = stopOnTimeout;
            return this;
        }

        public TaskPollingOptions build() {
            return new TaskPollingOptions(this);
        }
    }
}
