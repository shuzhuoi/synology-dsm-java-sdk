package io.github.shuzhuoi.synology.exception;

/**
 * JSON 语法或模型映射失败时抛出的统一 SDK 异常。
 * <p>
 * 具体 JSON 库异常只作为 cause 保留，不作为公共异常契约泄漏给调用方。
 */
public class SynologyJsonException extends SynologyDsmException {

    public SynologyJsonException(String message) {
        super(message);
    }

    public SynologyJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
