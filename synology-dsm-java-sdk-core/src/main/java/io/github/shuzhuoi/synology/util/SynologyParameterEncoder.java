package io.github.shuzhuoi.synology.util;

import io.github.shuzhuoi.synology.model.Additional;

import java.util.ArrayList;
import java.util.List;

/**
 * 官方文档中 path、additional 等参数经常使用 JSON-like 数组字符串，这里统一编码，避免业务类手写拼接。
 */
public final class SynologyParameterEncoder {

    private SynologyParameterEncoder() {
    }

    public static String booleanValue(Boolean value) {
        return value == null ? null : String.valueOf(value);
    }

    public static String integerValue(Integer value) {
        return value == null ? null : String.valueOf(value);
    }

    public static String longValue(Long value) {
        // Search 的 size_from/size_to、mtime_from/to 等参数可能超出 int 范围，统一用 Long 编码。
        return value == null ? null : String.valueOf(value);
    }

    public static String stringValue(String value) {
        return value;
    }

    public static String quoted(String value) {
        // Synology 文档中部分路径参数要求以 JSON 字符串形式传输，例如 "/photo/a.jpg"。
        if (value == null) {
            return null;
        }
        return "\"" + escape(value) + "\"";
    }

    public static String stringList(List<String> values) {
        // 多路径参数使用 ["path1","path2"] 这种 JSON-like 数组字符串。
        if (values == null || values.isEmpty()) {
            return null;
        }
        List<String> quotedValues = new ArrayList<String>();
        for (String value : values) {
            quotedValues.add(quoted(value));
        }
        return "[" + join(quotedValues) + "]";
    }

    public static String quotedOrList(List<String> values) {
        // 官方部分接口单值示例用 "id"，多值语义用 ["id1","id2"]；这里统一按数量选择编码。
        if (values == null || values.isEmpty()) {
            return null;
        }
        if (values.size() == 1) {
            return quoted(values.get(0));
        }
        return stringList(values);
    }

    public static String additionalList(List<Additional> values) {
        // additional 参数同样使用数组字符串，方便 DSM 返回扩展字段。
        if (values == null || values.isEmpty()) {
            return null;
        }
        List<String> names = new ArrayList<String>();
        for (Additional value : values) {
            if (value != null) {
                names.add(value.getValue());
            }
        }
        return stringList(names);
    }

    public static String join(List<String> values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(values.get(i));
        }
        return builder.toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
