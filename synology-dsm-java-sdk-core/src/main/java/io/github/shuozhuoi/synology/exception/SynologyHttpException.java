package io.github.shuzhuoi.synology.exception;

public class SynologyHttpException extends SynologyDsmException {

    public SynologyHttpException(String message) {
        super(message);
    }

    public SynologyHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
