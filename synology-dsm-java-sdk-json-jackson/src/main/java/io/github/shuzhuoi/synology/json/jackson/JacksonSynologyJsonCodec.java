package io.github.shuzhuoi.synology.json.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.shuzhuoi.synology.exception.SynologyJsonException;
import io.github.shuzhuoi.synology.json.SynologyJsonCodec;
import io.github.shuzhuoi.synology.json.SynologyJsonError;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 Jackson 的 Synology JSON Codec 默认实现。
 * <p>
 * 该类始终使用独立 ObjectMapper。传入用户 ObjectMapper 时会先复制，避免修改调用方全局配置。
 */
public class JacksonSynologyJsonCodec implements SynologyJsonCodec {

    private final ObjectMapper objectMapper;

    public JacksonSynologyJsonCodec() {
        this(new ObjectMapper());
    }

    public JacksonSynologyJsonCodec(ObjectMapper objectMapper) {
        if (objectMapper == null) {
            throw new IllegalArgumentException("objectMapper must not be null");
        }
        this.objectMapper = configure(objectMapper.copy());
    }

    @Override
    public <T> SynologyJsonResponse<T> decode(String body, Class<T> dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType must not be null");
        }
        try {
            JsonNode root = readRoot(body);
            boolean success = root.path("success").asBoolean(false);
            T data = null;
            JsonNode dataNode = root.get("data");
            if (success && dataType != Object.class && dataNode != null && !dataNode.isNull()) {
                data = objectMapper.treeToValue(dataNode, dataType);
            }
            return new SynologyJsonResponse<T>(success, data, readError(root, success));
        } catch (IOException | IllegalArgumentException e) {
            throw new SynologyJsonException("failed to decode Synology JSON response", e);
        }
    }

    @Override
    public <T> SynologyJsonResponse<Map<String, T>> decodeMap(String body, Class<T> valueType) {
        if (valueType == null) {
            throw new IllegalArgumentException("valueType must not be null");
        }
        try {
            JsonNode root = readRoot(body);
            boolean success = root.path("success").asBoolean(false);
            Map<String, T> data = null;
            JsonNode dataNode = root.get("data");
            if (success && dataNode != null && !dataNode.isNull()) {
                JavaType mapType = objectMapper.getTypeFactory()
                        .constructMapType(LinkedHashMap.class, String.class, valueType);
                data = objectMapper.convertValue(dataNode, mapType);
            }
            return new SynologyJsonResponse<Map<String, T>>(success, data, readError(root, success));
        } catch (IOException | IllegalArgumentException e) {
            throw new SynologyJsonException("failed to decode Synology JSON map response", e);
        }
    }

    private SynologyJsonError readError(JsonNode root, boolean success) {
        if (success) {
            return null;
        }
        JsonNode codeNode = root.path("error").path("code");
        Integer code = codeNode.isMissingNode() ? null : Integer.valueOf(codeNode.asInt());
        return new SynologyJsonError(code);
    }

    private JsonNode readRoot(String body) throws IOException {
        JsonNode root = objectMapper.readTree(body);
        if (root == null) {
            throw new IllegalArgumentException("Synology JSON response must not be empty");
        }
        return root;
    }

    private ObjectMapper configure(ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        mapper.setAnnotationIntrospector(AnnotationIntrospectorPair.pair(
                new SynologyAnnotationIntrospector(),
                mapper.getDeserializationConfig().getAnnotationIntrospector()
        ));
        return mapper;
    }
}
