package io.github.shuzhuoi.synology.internal;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shuzhuoi.synology.exception.SynologyApiException;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Synology DSM 标准 JSON 响应解析器。
 * <p>
 * 该类只在 SDK 内部使用，集中处理 success/data/error 结构、DSM 错误转换和实体映射，
 * 让执行器专注于请求编排。
 */
class SynologyResponseParser {

    private final ObjectMapper objectMapper;

    SynologyResponseParser() {
        this(SynologyObjectMapper.get());
    }

    SynologyResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 解析普通 JSON 接口返回值。
     * <p>
     * 保持原有语义：Object.class 和空 data 都返回 null。
     */
    <T> T parseJson(String apiName, String method, String body, Class<T> dataType) {
        JsonNode dataNode = parseDataNode(apiName, method, body);
        if (dataType == Object.class || dataNode == null || dataNode.isNull()) {
            return null;
        }
        return objectMapper.convertValue(dataNode, dataType);
    }

    /**
     * 解析 Map 形态的 data 节点，保留 LinkedHashMap 的顺序语义。
     */
    <T> Map<String, T> parseMap(String apiName, String method, String body, Class<T> valueType) {
        JsonNode dataNode = parseDataNode(apiName, method, body);
        JavaType mapType = objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, valueType);
        return objectMapper.convertValue(dataNode, mapType);
    }

    /**
     * 解析 multipart 等需要空 data 兜底实体的接口返回值。
     * <p>
     * 保持原有语义：Object.class 返回 null，空 data 创建空响应对象。
     */
    <T> T parseBody(String apiName, String method, String body, Class<T> dataType) {
        JsonNode dataNode = parseDataNode(apiName, method, body);
        if (dataType == Object.class) {
            return null;
        }
        if (dataNode == null || dataNode.isNull()) {
            return newEmptyResponse(apiName, method, dataType);
        }
        return objectMapper.convertValue(dataNode, dataType);
    }

    /**
     * 解析 DSM 标准响应并返回 data 节点。
     */
    JsonNode parseDataNode(String apiName, String method, String body) {
        // DSM 标准 JSON 响应形如 {"success":true,"data":{...}} 或 {"success":false,"error":{...}}。
        if (body == null || body.trim().length() == 0) {
            throw new SynologyHttpException("empty response body from " + apiName + "." + method);
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                JsonNode codeNode = root.path("error").path("code");
                Integer code = codeNode.isMissingNode() ? null : Integer.valueOf(codeNode.asInt());
                throw new SynologyApiException(apiName, method, code, body);
            }
            return root.get("data");
        } catch (IOException e) {
            throw new SynologyDsmException("failed to parse response from " + apiName + "." + method, e);
        }
    }

    private <T> T newEmptyResponse(String apiName, String method, Class<T> dataType) {
        try {
            return dataType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new SynologyDsmException("empty response data from " + apiName + "." + method, e);
        }
    }
}
