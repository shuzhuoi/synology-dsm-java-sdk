package io.github.shuzhuoi.synology.internal.request;

import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 API 请求实体的默认值、不可变性和 multipart 表达能力。
 */
class SynologyApiRequestTest {

    @Test
    void buildsImmutableTextRequestWithDefaults() {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", "/photo");
        parameters.put("ignored", null);

        SynologyApiRequest request = SynologyApiRequest.builder()
                .path("entry.cgi")
                .apiName("SYNO.Test")
                .version(1)
                .method("list")
                .parameters(parameters)
                .responseType(String.class)
                .build();
        parameters.put("path", "/changed");

        assertEquals("/photo", request.getParameters().get("path"));
        assertFalse(request.getParameters().containsKey("ignored"));
        assertEquals(SynologyHttpMethod.GET, request.getHttpMethod());
        assertEquals(ResponseBodyMode.TEXT, request.getResponseBodyMode());
        assertFalse(request.isMultipart());
        assertThrows(UnsupportedOperationException.class, () -> request.getParameters().put("new", "value"));
    }

    @Test
    void buildsMultipartStreamRequest() {
        SynologyApiRequest request = SynologyApiRequest.builder()
                .path("entry.cgi")
                .apiName("SYNO.Test")
                .version(2)
                .method("upload")
                .authenticated(true)
                .parameter("path", "/target")
                .httpMethod(SynologyHttpMethod.POST)
                .responseBodyMode(ResponseBodyMode.STREAM)
                .multipartPart(SynologyMultipartPart.text("file", "content"))
                .build();

        assertTrue(request.isAuthenticated());
        assertTrue(request.isMultipart());
        assertEquals(1, request.getMultipartParts().size());
        assertEquals("/target", request.getParameters().get("path"));
    }

    @Test
    void rejectsMissingRequiredMetadata() {
        assertThrows(IllegalStateException.class, () -> SynologyApiRequest.builder()
                .apiName("SYNO.Test")
                .version(1)
                .method("list")
                .build());
    }
}
