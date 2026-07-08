package io.github.shuzhuoi.synology.exception;

public class SynologyDsmException extends RuntimeException {

    public SynologyDsmException(String message) {
        super(message);
    }

    public SynologyDsmException(String message, Throwable cause) {
        super(message, cause);
    }
}
