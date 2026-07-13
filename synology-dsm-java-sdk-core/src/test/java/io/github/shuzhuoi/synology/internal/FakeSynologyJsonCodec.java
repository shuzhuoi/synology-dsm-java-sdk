package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.exception.SynologyJsonException;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonError;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * core 测试使用的最小 Codec fake，验证执行器和解析器不依赖具体 JSON 库。
 */
class FakeSynologyJsonCodec implements SynologyJsonCodec {

    @Override
    @SuppressWarnings("unchecked")
    public <T> SynologyJsonResponse<T> decode(String body, Class<T> dataType) {
        checkBody(body);
        if (body.contains("not-json")) {
            throw new SynologyJsonException("invalid test JSON", null);
        }
        if (body.contains("\"success\":false")) {
            Integer code = body.contains("119") ? Integer.valueOf(119) : null;
            return new SynologyJsonResponse<T>(false, null, new SynologyJsonError(code));
        }
        if (dataType == Object.class || !body.contains("\"data\"")) {
            return new SynologyJsonResponse<T>(true, null, null);
        }
        if (dataType == ExecutorData.class) {
            ExecutorData data = new ExecutorData();
            if (body.contains("\"name\":\"alpha\"")) {
                data.setName("alpha");
            }
            if (body.contains("\"count\":1")) {
                data.setCount(Integer.valueOf(1));
            } else if (body.contains("\"count\":3")) {
                data.setCount(Integer.valueOf(3));
            } else if (body.contains("\"count\":7")) {
                data.setCount(Integer.valueOf(7));
            } else if (body.contains("\"count\":9")) {
                data.setCount(Integer.valueOf(9));
            }
            return new SynologyJsonResponse<T>(true, (T) data, null);
        }
        return new SynologyJsonResponse<T>(true, null, null);
    }

    @Override
    public <T> SynologyJsonResponse<Map<String, T>> decodeMap(String body, Class<T> valueType) {
        checkBody(body);
        if (body.contains("\"success\":false")) {
            return new SynologyJsonResponse<Map<String, T>>(false, null, new SynologyJsonError(119));
        }
        Map<String, T> data = new LinkedHashMap<String, T>();
        if (valueType == ExecutorData.class && body.contains("first")) {
            ExecutorData item = new ExecutorData();
            item.setName("alpha");
            item.setCount(Integer.valueOf(1));
            data.put("first", valueType.cast(item));
        }
        return new SynologyJsonResponse<Map<String, T>>(true, data, null);
    }

    private void checkBody(String body) {
        if (body == null || body.trim().length() == 0) {
            throw new SynologyJsonException("empty test body", null);
        }
    }
}
