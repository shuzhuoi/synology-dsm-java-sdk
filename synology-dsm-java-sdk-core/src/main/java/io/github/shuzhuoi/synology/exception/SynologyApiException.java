package io.github.shuzhuoi.synology.exception;

public class SynologyApiException extends SynologyDsmException {

    private final String apiName;
    private final String method;
    private final Integer errorCode;
    private final String rawBody;

    public SynologyApiException(String apiName, String method, Integer errorCode, String rawBody) {
        super("Synology API call failed: api=" + apiName + ", method=" + method + ", code=" + errorCode);
        this.apiName = apiName;
        this.method = method;
        this.errorCode = errorCode;
        this.rawBody = rawBody;
    }

    public String getApiName() {
        return apiName;
    }

    public String getMethod() {
        return method;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getRawBody() {
        return rawBody;
    }
}
