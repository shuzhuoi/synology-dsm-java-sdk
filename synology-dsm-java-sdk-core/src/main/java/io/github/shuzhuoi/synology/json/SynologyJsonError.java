package io.github.shuzhuoi.synology.json;

/**
 * DSM 标准错误节点中与 core 执行管线有关的信息。
 */
public final class SynologyJsonError {

    private final Integer code;

    public SynologyJsonError(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
