package io.github.shuzhuoi.synology.util;

import io.github.shuzhuoi.synology.model.Additional;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 固定当前参数编码规则，避免后续重构业务 Client 时改变 DSM 请求参数格式。
 */
class SynologyParameterEncoderTest {

    @Test
    void encodesScalarValues() {
        assertEquals("true", SynologyParameterEncoder.booleanValue(Boolean.TRUE));
        assertEquals("12", SynologyParameterEncoder.integerValue(Integer.valueOf(12)));
        assertEquals("1234567890123", SynologyParameterEncoder.longValue(Long.valueOf(1234567890123L)));
        assertEquals("plain", SynologyParameterEncoder.stringValue("plain"));

        assertNull(SynologyParameterEncoder.booleanValue(null));
        assertNull(SynologyParameterEncoder.integerValue(null));
        assertNull(SynologyParameterEncoder.longValue(null));
        assertNull(SynologyParameterEncoder.stringValue(null));
    }

    @Test
    void encodesQuotedStringAndEscapesSpecialCharacters() {
        assertEquals("\"/photo/a.jpg\"", SynologyParameterEncoder.quoted("/photo/a.jpg"));
        assertEquals("\"a\\\\b\\\"c\"", SynologyParameterEncoder.quoted("a\\b\"c"));
        assertNull(SynologyParameterEncoder.quoted(null));
    }

    @Test
    void encodesStringLists() {
        assertEquals("[\"/a\",\"/b\"]", SynologyParameterEncoder.stringList(Arrays.asList("/a", "/b")));
        assertEquals("[\"/a\",null]", SynologyParameterEncoder.stringList(Arrays.asList("/a", null)));

        assertNull(SynologyParameterEncoder.stringList(null));
        assertNull(SynologyParameterEncoder.stringList(Collections.<String>emptyList()));
    }

    @Test
    void encodesQuotedOrListByValueCount() {
        assertEquals("\"task-1\"", SynologyParameterEncoder.quotedOrList(Collections.singletonList("task-1")));
        assertEquals("[\"task-1\",\"task-2\"]", SynologyParameterEncoder.quotedOrList(Arrays.asList("task-1", "task-2")));

        assertNull(SynologyParameterEncoder.quotedOrList(null));
        assertNull(SynologyParameterEncoder.quotedOrList(Collections.<String>emptyList()));
    }

    @Test
    void encodesIntegerListsAndSkipsNullValues() {
        assertEquals("1,2", SynologyParameterEncoder.integerList(Arrays.asList(Integer.valueOf(1), null, Integer.valueOf(2))));

        assertNull(SynologyParameterEncoder.integerList(null));
        assertNull(SynologyParameterEncoder.integerList(Collections.<Integer>emptyList()));
        assertNull(SynologyParameterEncoder.integerList(Arrays.<Integer>asList(null, null)));
    }

    @Test
    void encodesAdditionalListAndSkipsNullValues() {
        assertEquals("[\"size\",\"owner\"]",
                SynologyParameterEncoder.additionalList(Arrays.asList(Additional.SIZE, null, Additional.OWNER)));

        assertNull(SynologyParameterEncoder.additionalList(null));
        assertNull(SynologyParameterEncoder.additionalList(Collections.<Additional>emptyList()));
    }
}
