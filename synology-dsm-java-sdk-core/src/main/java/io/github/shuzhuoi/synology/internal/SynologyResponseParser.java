package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.exception.SynologyApiException;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.exception.SynologyJsonException;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonError;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;

import java.util.Map;

/**
 * Synology DSM 标准 JSON 响应解析器。
 * <p>
 * 该类只在 SDK 内部使用，集中处理 success/data/error 结构、DSM 错误转换和实体映射，
 * 让执行器专注于请求编排。
 */
class SynologyResponseParser {

    private final SynologyJsonCodec jsonCodec;

    SynologyResponseParser(SynologyJsonCodec jsonCodec) {
        if (jsonCodec == null) {
            throw new IllegalArgumentException("jsonCodec must not be null");
        }
        this.jsonCodec = jsonCodec;
    }

    /**
     * 解析普通 JSON 接口返回值。
     * <p>
     * 保持原有语义：Object.class 和空 data 都返回 null。
     */
    <T> T parseJson(String apiName, String method, String body, Class<T> dataType) {
        SynologyJsonResponse<T> response = decode(apiName, method, body, dataType);
        if (dataType == Object.class || response.getData() == null) {
            return null;
        }
        return response.getData();
    }

    /**
     * 解析 Map 形态的 data 节点，保留 LinkedHashMap 的顺序语义。
     */
    <T> Map<String, T> parseMap(String apiName, String method, String body, Class<T> valueType) {
        checkBody(apiName, method, body);
        try {
            SynologyJsonResponse<Map<String, T>> response = jsonCodec.decodeMap(body, valueType);
            checkSuccess(apiName, method, body, response);
            return response.getData();
        } catch (SynologyJsonException e) {
            throw parseException(apiName, method, e);
        }
    }

    /**
     * 解析 multipart 等需要空 data 兜底实体的接口返回值。
     * <p>
     * 保持原有语义：Object.class 返回 null，空 data 创建空响应对象。
     */
    <T> T parseBody(String apiName, String method, String body, Class<T> dataType) {
        SynologyJsonResponse<T> response = decode(apiName, method, body, dataType);
        if (dataType == Object.class) {
            return null;
        }
        if (response.getData() == null) {
            return newEmptyResponse(apiName, method, dataType);
        }
        return response.getData();
    }

    /**
     * 解析 DSM 标准响应，并统一转换错误和具体 JSON 库异常。
     */
    private <T> SynologyJsonResponse<T> decode(String apiName, String method, String body, Class<T> dataType) {
        checkBody(apiName, method, body);
        try {
            SynologyJsonResponse<T> response = jsonCodec.decode(body, dataType);
            checkSuccess(apiName, method, body, response);
            return response;
        } catch (SynologyJsonException e) {
            throw parseException(apiName, method, e);
        }
    }

    private void checkBody(String apiName, String method, String body) {
        if (body == null || body.trim().length() == 0) {
            throw new SynologyHttpException("empty response body from " + apiName + "." + method);
        }
    }

    private void checkSuccess(
            String apiName,
            String method,
            String body,
            SynologyJsonResponse<?> response) {
        if (response == null) {
            throw new SynologyDsmException("JSON codec returned null response from " + apiName + "." + method);
        }
        if (!response.isSuccess()) {
            SynologyJsonError error = response.getError();
            Integer code = error == null ? null : error.getCode();
            throw new SynologyApiException(apiName, method, code, body);
        }
    }

    private SynologyDsmException parseException(String apiName, String method, SynologyJsonException cause) {
        return new SynologyDsmException("failed to parse response from " + apiName + "." + method, cause);
    }

    private <T> T newEmptyResponse(String apiName, String method, Class<T> dataType) {
        try {
            return dataType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new SynologyDsmException("empty response data from " + apiName + "." + method, e);
        }
    }
}
