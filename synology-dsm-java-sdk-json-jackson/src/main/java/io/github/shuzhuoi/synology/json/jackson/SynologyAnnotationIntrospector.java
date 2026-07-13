package io.github.shuzhuoi.synology.json.jackson;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import io.github.shuzhuoi.synology.json.annotation.SynologyJsonAlias;
import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import io.github.shuzhuoi.synology.json.annotation.SynologyJsonStringList;

import java.util.Collections;
import java.util.List;

/**
 * 将 core 的中立映射注解转换为 Jackson 反序列化规则。
 */
final class SynologyAnnotationIntrospector extends NopAnnotationIntrospector {

    private static final long serialVersionUID = 1L;

    @Override
    public PropertyName findNameForDeserialization(Annotated annotated) {
        SynologyJsonProperty property = annotated.getAnnotation(SynologyJsonProperty.class);
        return property == null ? null : PropertyName.construct(property.value());
    }

    @Override
    public List<PropertyName> findPropertyAliases(Annotated annotated) {
        SynologyJsonAlias alias = annotated.getAnnotation(SynologyJsonAlias.class);
        return alias == null
                ? null
                : Collections.singletonList(PropertyName.construct(alias.value()));
    }

    @Override
    public Object findDeserializer(Annotated annotated) {
        SynologyJsonStringList stringList = annotated.getAnnotation(SynologyJsonStringList.class);
        return stringList == null ? null : SynologyStringListDeserializer.class;
    }
}
