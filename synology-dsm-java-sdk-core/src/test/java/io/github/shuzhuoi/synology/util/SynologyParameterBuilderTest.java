package io.github.shuzhuoi.synology.util;

import io.github.shuzhuoi.synology.model.Additional;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 验证参数构建器只做组装，不改变 {@link SynologyParameterEncoder} 的编码语义。
 */
class SynologyParameterBuilderTest {

    @Test
    void buildsParametersWithEncoderRules() {
        Map<String, String> parameters = SynologyParameterBuilder.create()
                .putString("keyword", "photo")
                .putQuoted("path", "/photo/a.jpg")
                .putBoolean("recursive", Boolean.TRUE)
                .putInteger("limit", Integer.valueOf(100))
                .putLong("mtime_from", Long.valueOf(123456789L))
                .putStringList("paths", Arrays.asList("/a", "/b"))
                .putQuotedOrList("taskid", Arrays.asList("t1", "t2"))
                .putIntegerList("item_id", Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)))
                .putAdditionalList("additional", Arrays.asList(Additional.SIZE, Additional.TIME))
                .build();

        assertEquals("photo", parameters.get("keyword"));
        assertEquals("\"/photo/a.jpg\"", parameters.get("path"));
        assertEquals("true", parameters.get("recursive"));
        assertEquals("100", parameters.get("limit"));
        assertEquals("123456789", parameters.get("mtime_from"));
        assertEquals("[\"/a\",\"/b\"]", parameters.get("paths"));
        assertEquals("[\"t1\",\"t2\"]", parameters.get("taskid"));
        assertEquals("1,2", parameters.get("item_id"));
        assertEquals("[\"size\",\"time\"]", parameters.get("additional"));
    }

    @Test
    void skipsNullValuesLikeExecutorFinalRequestParameters() {
        Map<String, String> existing = new LinkedHashMap<String, String>();
        existing.put("kept", "value");
        existing.put("ignored", null);

        Map<String, String> parameters = SynologyParameterBuilder.builder()
                .putString("name", null)
                .putQuoted("path", null)
                .putBoolean("recursive", null)
                .putAll(existing)
                .build();

        assertEquals(1, parameters.size());
        assertEquals("value", parameters.get("kept"));
        assertFalse(parameters.containsKey("ignored"));
    }

    @Test
    void buildReturnsDefensiveCopy() {
        SynologyParameterBuilder builder = SynologyParameterBuilder.create()
                .putString("name", "first");

        Map<String, String> first = builder.build();
        first.put("name", "changed");

        Map<String, String> second = builder.build();
        assertEquals("first", second.get("name"));
    }
}
