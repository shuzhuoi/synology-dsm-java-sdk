package io.github.shuzhuoi.synology.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 兼容 DSM 字段在不同版本中返回字符串或数组的情况。
 * 例如 support_virtual_protocol 可能返回 "cifs,iso"，也可能返回 ["cifs", "iso"]。
 */
public class SynologyStringListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_STRING) {
            return splitString(parser.getValueAsString());
        }
        if (token == JsonToken.START_ARRAY) {
            return readArray(parser);
        }
        if (token == JsonToken.VALUE_NULL) {
            return Collections.emptyList();
        }
        return Collections.singletonList(parser.getValueAsString());
    }

    private List<String> splitString(String value) {
        if (value == null || value.trim().length() == 0) {
            return Collections.emptyList();
        }
        String[] items = value.split(",");
        List<String> values = new ArrayList<String>();
        for (String item : items) {
            String trimmed = item.trim();
            if (trimmed.length() > 0) {
                values.add(trimmed);
            }
        }
        return values;
    }

    private List<String> readArray(JsonParser parser) throws IOException {
        List<String> values = new ArrayList<String>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            String value = parser.getValueAsString();
            if (value != null && value.trim().length() > 0) {
                values.add(value.trim());
            }
        }
        return values;
    }
}
