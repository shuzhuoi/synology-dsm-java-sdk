package io.github.shuzhuoi.synology.internal;

import io.github.shuzhuoi.synology.exception.SynologyApiException;
import io.github.shuzhuoi.synology.exception.SynologyDsmException;
import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖 SynologyResponseParser 的边界行为，保证从执行器抽取后语义不变。
 */
class SynologyResponseParserTest {

    private final SynologyResponseParser parser = new SynologyResponseParser();

    @Test
    void parseJsonConvertsDataToEntity() {
        ExecutorData data = parser.parseJson(
                "SYNO.Test",
                "list",
                "{\"success\":true,\"data\":{\"name\":\"alpha\",\"count\":3}}",
                ExecutorData.class
        );

        assertEquals("alpha", data.getName());
        assertEquals(Integer.valueOf(3), data.getCount());
    }

    @Test
    void parseJsonReturnsNullForObjectType() {
        Object data = parser.parseJson(
                "SYNO.Test",
                "noop",
                "{\"success\":true,\"data\":{\"name\":\"alpha\"}}",
                Object.class
        );

        assertNull(data);
    }

    @Test
    void parseJsonReturnsNullWhenDataIsMissing() {
        ExecutorData data = parser.parseJson(
                "SYNO.Test",
                "noop",
                "{\"success\":true}",
                ExecutorData.class
        );

        assertNull(data);
    }

    @Test
    void parseBodyCreatesEmptyEntityWhenDataIsMissing() {
        ExecutorData data = parser.parseBody(
                "SYNO.Test",
                "upload",
                "{\"success\":true}",
                ExecutorData.class
        );

        assertNull(data.getName());
        assertNull(data.getCount());
    }

    @Test
    void parseMapKeepsLinkedHashMapSemantics() {
        Map<String, ExecutorData> data = parser.parseMap(
                "SYNO.Test",
                "map",
                "{\"success\":true,\"data\":{\"first\":{\"name\":\"alpha\",\"count\":1}}}",
                ExecutorData.class
        );

        assertTrue(data instanceof LinkedHashMap);
        assertEquals("alpha", data.get("first").getName());
    }

    @Test
    void parseDataNodeThrowsApiExceptionWhenDsmReportsFailure() {
        SynologyApiException exception = assertThrows(SynologyApiException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                parser.parseDataNode("SYNO.Test", "list", "{\"success\":false,\"error\":{\"code\":119}}");
            }
        });

        assertEquals("SYNO.Test", exception.getApiName());
        assertEquals("list", exception.getMethod());
        assertEquals(Integer.valueOf(119), exception.getErrorCode());
    }

    @Test
    void parseDataNodeKeepsNullErrorCodeWhenCodeIsMissing() {
        SynologyApiException exception = assertThrows(SynologyApiException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                parser.parseDataNode("SYNO.Test", "list", "{\"success\":false,\"error\":{}}");
            }
        });

        assertNull(exception.getErrorCode());
    }

    @Test
    void parseDataNodeThrowsHttpExceptionWhenBodyIsEmpty() {
        assertThrows(SynologyHttpException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                parser.parseDataNode("SYNO.Test", "list", " ");
            }
        });
    }

    @Test
    void parseDataNodeThrowsDsmExceptionWhenBodyIsNotJson() {
        assertThrows(SynologyDsmException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                parser.parseDataNode("SYNO.Test", "list", "not-json");
            }
        });
    }
}
