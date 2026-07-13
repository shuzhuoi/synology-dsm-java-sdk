package io.github.shuzhuoi.synology.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将字符串、逗号分隔字符串或 JSON 数组统一映射为字符串列表。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SynologyJsonStringList {
}
