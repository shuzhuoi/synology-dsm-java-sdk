package io.github.shuzhuoi.synology.json;

/**
 * 与具体 JSON 库无关的 DSM 标准响应。
 *
 * @param <T> data 节点类型
 */
public final class SynologyJsonResponse<T> {

    private final boolean success;
    private final T data;
    private final SynologyJsonError error;

    public SynologyJsonResponse(boolean success, T data, SynologyJsonError error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public SynologyJsonError getError() {
        return error;
    }
}
