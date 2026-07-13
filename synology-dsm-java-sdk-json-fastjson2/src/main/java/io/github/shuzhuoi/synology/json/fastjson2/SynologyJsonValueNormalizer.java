package io.github.shuzhuoi.synology.json.fastjson2;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonAlias;
import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import io.github.shuzhuoi.synology.json.annotation.SynologyJsonStringList;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 core 的中立 JSON 注解转换为 Fastjson2 可识别的 Java 属性结构。
 * <p>
 * 归一化只处理 JSON 中已有的数据，不访问或修改目标实体实例。
 */
final class SynologyJsonValueNormalizer {

    private static final Object ABSENT = new Object();

    Object normalize(Object value, Type targetType) {
        if (value == null || targetType == null) {
            return value;
        }
        if (targetType instanceof ParameterizedType) {
            return normalizeParameterized(value, (ParameterizedType) targetType);
        }
        if (targetType instanceof GenericArrayType) {
            return normalizeCollection(value, ((GenericArrayType) targetType).getGenericComponentType());
        }
        if (!(targetType instanceof Class<?>)) {
            return value;
        }
        Class<?> targetClass = (Class<?>) targetType;
        if (targetClass.isArray()) {
            return normalizeCollection(value, targetClass.getComponentType());
        }
        if (value instanceof Map && isModelType(targetClass)) {
            return normalizeObject((Map<?, ?>) value, targetClass);
        }
        return value;
    }

    private Object normalizeParameterized(Object value, ParameterizedType targetType) {
        Type rawType = targetType.getRawType();
        if (!(rawType instanceof Class<?>)) {
            return value;
        }
        Class<?> rawClass = (Class<?>) rawType;
        Type[] arguments = targetType.getActualTypeArguments();
        if (Collection.class.isAssignableFrom(rawClass) && arguments.length == 1) {
            return normalizeCollection(value, arguments[0]);
        }
        if (Map.class.isAssignableFrom(rawClass) && arguments.length == 2) {
            return normalizeMapValues(value, arguments[1]);
        }
        return normalize(value, rawClass);
    }

    private Map<String, Object> normalizeObject(Map<?, ?> source, Class<?> targetClass) {
        Map<String, Object> normalized = copyMap(source);
        for (Field field : fieldsOf(targetClass)) {
            Object value = findPropertyValue(source, field);
            if (value == ABSENT) {
                continue;
            }
            if (field.isAnnotationPresent(SynologyJsonStringList.class)) {
                value = normalizeStringList(value);
            } else {
                value = normalize(value, field.getGenericType());
            }
            normalized.put(field.getName(), value);
        }
        return normalized;
    }

    private Object findPropertyValue(Map<?, ?> source, Field field) {
        SynologyJsonProperty property = field.getAnnotation(SynologyJsonProperty.class);
        if (property != null && source.containsKey(property.value())) {
            return source.get(property.value());
        }
        if (source.containsKey(field.getName())) {
            return source.get(field.getName());
        }
        SynologyJsonAlias alias = field.getAnnotation(SynologyJsonAlias.class);
        if (alias != null && source.containsKey(alias.value())) {
            return source.get(alias.value());
        }
        return ABSENT;
    }

    private Object normalizeCollection(Object value, Type elementType) {
        if (!(value instanceof Collection<?>)) {
            return value;
        }
        List<Object> normalized = new ArrayList<Object>();
        for (Object item : (Collection<?>) value) {
            normalized.add(normalize(item, elementType));
        }
        return normalized;
    }

    private Object normalizeMapValues(Object value, Type valueType) {
        if (!(value instanceof Map<?, ?>)) {
            return value;
        }
        Map<String, Object> normalized = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            normalized.put(String.valueOf(entry.getKey()), normalize(entry.getValue(), valueType));
        }
        return normalized;
    }

    private List<String> normalizeStringList(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof Collection<?>) {
            List<String> result = new ArrayList<String>();
            for (Object item : (Collection<?>) value) {
                addTrimmed(result, item);
            }
            return result;
        }
        if (value instanceof String) {
            List<String> result = new ArrayList<String>();
            for (String item : ((String) value).split(",")) {
                addTrimmed(result, item);
            }
            return result;
        }
        return Collections.singletonList(String.valueOf(value));
    }

    private void addTrimmed(List<String> values, Object item) {
        if (item == null) {
            return;
        }
        String trimmed = String.valueOf(item).trim();
        if (trimmed.length() > 0) {
            values.add(trimmed);
        }
    }

    private Map<String, Object> copyMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            result.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }

    private List<Field> fieldsOf(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    private boolean isModelType(Class<?> type) {
        return type != Object.class
                && !type.isPrimitive()
                && !type.isEnum()
                && !type.getName().startsWith("java.");
    }

}
