package io.github.shuzhuoi.synology.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class SynologyObjectMapper {

    private static final ObjectMapper OBJECT_MAPPER = create();

    private SynologyObjectMapper() {
    }

    public static ObjectMapper get() {
        return OBJECT_MAPPER;
    }

    private static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
