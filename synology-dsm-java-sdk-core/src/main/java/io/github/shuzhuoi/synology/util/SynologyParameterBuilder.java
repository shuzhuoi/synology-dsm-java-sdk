package io.github.shuzhuoi.synology.util;

import io.github.shuzhuoi.synology.model.Additional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DSM API 参数构建器，统一复用 {@link SynologyParameterEncoder} 的编码规则。
 * <p>
 * 该类只负责把业务参数组装成 SDK 内部统一使用的 {@code Map<String, String>}，
 * 不负责追加 api、version、method、_sid 等通用参数。
 */
public final class SynologyParameterBuilder {

    private final Map<String, String> parameters = new LinkedHashMap<String, String>();

    private SynologyParameterBuilder() {
    }

    public static SynologyParameterBuilder create() {
        return new SynologyParameterBuilder();
    }

    /**
     * 提供与其他 builder API 更一致的入口，便于调用方链式构造参数。
     */
    public static SynologyParameterBuilder builder() {
        return create();
    }

    /**
     * 写入已经编码好的参数值。
     * <p>
     * null value 会被跳过，保持与执行器最终请求参数合并逻辑一致。
     */
    public SynologyParameterBuilder put(String name, String value) {
        if (value != null) {
            parameters.put(name, value);
        }
        return this;
    }

    /**
     * 写入普通字符串参数，不额外增加 JSON 引号。
     */
    public SynologyParameterBuilder putString(String name, String value) {
        return put(name, SynologyParameterEncoder.stringValue(value));
    }

    /**
     * 写入需要 JSON 字符串形式的参数，例如部分 path、taskid 参数。
     */
    public SynologyParameterBuilder putQuoted(String name, String value) {
        return put(name, SynologyParameterEncoder.quoted(value));
    }

    /**
     * 写入布尔参数，编码结果为 true/false 字符串。
     */
    public SynologyParameterBuilder putBoolean(String name, Boolean value) {
        return put(name, SynologyParameterEncoder.booleanValue(value));
    }

    /**
     * 写入整数参数。
     */
    public SynologyParameterBuilder putInteger(String name, Integer value) {
        return put(name, SynologyParameterEncoder.integerValue(value));
    }

    /**
     * 写入长整数参数，适用于时间戳、文件大小等可能超过 int 的字段。
     */
    public SynologyParameterBuilder putLong(String name, Long value) {
        return put(name, SynologyParameterEncoder.longValue(value));
    }

    /**
     * 写入字符串列表参数，编码结果形如 ["a","b"]。
     */
    public SynologyParameterBuilder putStringList(String name, List<String> values) {
        return put(name, SynologyParameterEncoder.stringList(values));
    }

    /**
     * 写入单值或多值参数：单个值编码为 "a"，多个值编码为 ["a","b"]。
     */
    public SynologyParameterBuilder putQuotedOrList(String name, List<String> values) {
        return put(name, SynologyParameterEncoder.quotedOrList(values));
    }

    /**
     * 写入整数列表参数，编码结果为逗号分隔字符串。
     */
    public SynologyParameterBuilder putIntegerList(String name, List<Integer> values) {
        return put(name, SynologyParameterEncoder.integerList(values));
    }

    /**
     * 写入 File Station additional 参数。
     */
    public SynologyParameterBuilder putAdditionalList(String name, List<Additional> values) {
        return put(name, SynologyParameterEncoder.additionalList(values));
    }

    /**
     * 批量写入已经编码好的参数，null value 仍会被跳过。
     */
    public SynologyParameterBuilder putAll(Map<String, String> values) {
        if (values == null) {
            return this;
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 返回参数副本，避免调用方修改 builder 内部状态。
     */
    public Map<String, String> build() {
        return new LinkedHashMap<String, String>(parameters);
    }
}
