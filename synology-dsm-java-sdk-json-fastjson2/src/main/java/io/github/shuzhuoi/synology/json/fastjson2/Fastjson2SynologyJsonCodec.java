package io.github.shuzhuoi.synology.json.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.shuzhuoi.synology.exception.SynologyJsonException;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonError;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 Fastjson2 的 Synology JSON Codec 实现。
 * <p>
 * 该实现只在模块内部使用 Fastjson2 类型，对外仍然遵守 core 定义的中立 JSON 契约。
 */
public class Fastjson2SynologyJsonCodec implements SynologyJsonCodec {

    private final SynologyJsonValueNormalizer valueNormalizer = new SynologyJsonValueNormalizer();

    @Override
    public <T> SynologyJsonResponse<T> decode(String body, Class<T> dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType must not be null");
        }
        try {
            JSONObject root = readRoot(body);
            boolean success = readSuccess(root);
            T data = null;
            Object dataValue = root.get("data");
            if (success && dataType != Object.class && dataValue != null) {
                data = convert(dataValue, dataType);
            }
            return new SynologyJsonResponse<T>(success, data, readError(root, success));
        } catch (SynologyJsonException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SynologyJsonException("failed to decode Synology JSON response", e);
        }
    }

    @Override
    public <T> SynologyJsonResponse<Map<String, T>> decodeMap(String body, Class<T> valueType) {
        if (valueType == null) {
            throw new IllegalArgumentException("valueType must not be null");
        }
        try {
            JSONObject root = readRoot(body);
            boolean success = readSuccess(root);
            Map<String, T> data = null;
            Object dataValue = root.get("data");
            if (success && dataValue != null) {
                data = convertMap(dataValue, valueType);
            }
            return new SynologyJsonResponse<Map<String, T>>(success, data, readError(root, success));
        } catch (SynologyJsonException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SynologyJsonException("failed to decode Synology JSON map response", e);
        }
    }

    private <T> T convert(Object value, Class<T> targetType) {
        Object normalized = valueNormalizer.normalize(value, targetType);
        return JSON.parseObject(JSON.toJSONString(normalized), targetType);
    }

    private <T> Map<String, T> convertMap(Object value, Class<T> valueType) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Synology JSON data must be an object");
        }
        Map<String, T> result = new LinkedHashMap<String, T>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            result.put(String.valueOf(entry.getKey()), convert(entry.getValue(), valueType));
        }
        return result;
    }

    private JSONObject readRoot(String body) {
        JSONObject root = JSON.parseObject(body);
        if (root == null) {
            throw new IllegalArgumentException("Synology JSON response must not be empty");
        }
        return root;
    }

    private boolean readSuccess(JSONObject root) {
        Object success = root.get("success");
        return Boolean.TRUE.equals(success) || "true".equalsIgnoreCase(String.valueOf(success));
    }

    private SynologyJsonError readError(JSONObject root, boolean success) {
        if (success) {
            return null;
        }
        Object errorValue = root.get("error");
        if (!(errorValue instanceof Map)) {
            return new SynologyJsonError(null);
        }
        Object codeValue = ((Map<?, ?>) errorValue).get("code");
        return new SynologyJsonError(toInteger(codeValue));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        return Integer.valueOf(String.valueOf(value));
    }
}
