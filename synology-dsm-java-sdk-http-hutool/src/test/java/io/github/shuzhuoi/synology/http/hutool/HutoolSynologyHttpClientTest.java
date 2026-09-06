package io.github.shuzhuoi.synology.http.hutool;

import io.github.shuzhuoi.synology.exception.SynologyHttpException;
import io.github.shuzhuoi.synology.http.ResponseBodyMode;
import io.github.shuzhuoi.synology.http.SynologyHttpMethod;
import io.github.shuzhuoi.synology.http.SynologyHttpRequest;
import io.github.shuzhuoi.synology.http.SynologyHttpResponse;
import io.github.shuzhuoi.synology.http.SynologyMultipartPart;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link HutoolSynologyHttpClient} 适配层测试，使用本地 MockWebServer 验证请求转换与响应读取行为。
 */
class HutoolSynologyHttpClientTest {

    private MockWebServer server;
    private HutoolSynologyHttpClient httpClient;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        httpClient = new HutoolSynologyHttpClient();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    /**
     * GET 请求的参数应由适配器拼接到 URL 查询串，文本响应应完整读取。
     */
    @Test
    void shouldAppendGetParametersAndReadTextResponse() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("X-Synology-Test", "ok")
                .setBody("{\"success\":true}"));

        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.GET)
                .url(server.url("/webapi/entry.cgi").toString())
                .parameter("api", "SYNO.FileStation.List")
                .parameter("path", "/测试 folder")
                .build();

        SynologyHttpResponse response = httpClient.execute(request);
        RecordedRequest recordedRequest = server.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("SYNO.FileStation.List", recordedRequest.getRequestUrl().queryParameter("api"));
        assertEquals("/测试 folder", recordedRequest.getRequestUrl().queryParameter("path"));
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"success\":true}", response.getBody());
        assertEquals("ok", response.getHeaders().get("X-Synology-Test").get(0));
        assertNull(response.getBodyStream());
    }

    /**
     * POST 请求的普通参数应以 x-www-form-urlencoded 表单发送，中文与空格需正确编码。
     */
    @Test
    void shouldSendPostParametersAsFormBody() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"success\":true}"));

        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.POST)
                .url(server.url("/webapi/entry.cgi").toString())
                .parameter("api", "SYNO.FileStation.Test")
                .parameter("name", "测试 file")
                .build();

        httpClient.execute(request);
        RecordedRequest recordedRequest = server.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertTrue(recordedRequest.getHeader("Content-Type").startsWith("application/x-www-form-urlencoded"));
        Map<String, String> form = parseForm(recordedRequest.getBody().readUtf8());
        assertEquals("SYNO.FileStation.Test", form.get("api"));
        assertEquals("测试 file", form.get("name"));
    }

    /**
     * multipart 请求应包含普通字段和文件字段，文件内容以原始字节发送。
     */
    @Test
    void shouldSendParametersAndFileAsMultipart(@TempDir Path tempDir) throws Exception {
        server.enqueue(new MockResponse().setBody("{\"success\":true}"));
        Path filePath = tempDir.resolve("upload.txt");
        Files.write(filePath, "file-content".getBytes(StandardCharsets.UTF_8));
        File uploadFile = filePath.toFile();

        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.POST)
                .url(server.url("/webapi/entry.cgi").toString())
                .parameter("api", "SYNO.FileStation.Upload")
                .parameter("path", "/target")
                .multipartPart(SynologyMultipartPart.file("file", uploadFile))
                .build();

        httpClient.execute(request);
        RecordedRequest recordedRequest = server.takeRequest();
        String requestBody = recordedRequest.getBody().readUtf8();

        assertTrue(recordedRequest.getHeader("Content-Type").startsWith("multipart/form-data"));
        assertTrue(requestBody.contains("name=\"api\""));
        assertTrue(requestBody.contains("name=\"path\""));
        assertTrue(requestBody.contains("name=\"file\"; filename=\"upload.txt\""));
        assertTrue(requestBody.contains("file-content"));
    }

    /**
     * STREAM 模式下适配器应保留原始流且不读取文本内容，流在调用方关闭前保持可读。
     */
    @Test
    void shouldKeepStreamReadableUntilCallerClosesIt() throws IOException {
        server.enqueue(new MockResponse().setBody("binary-content"));

        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.GET)
                .url(server.url("/webapi/entry.cgi").toString())
                .responseBodyMode(ResponseBodyMode.STREAM)
                .build();

        SynologyHttpResponse response = httpClient.execute(request);

        assertNull(response.getBody());
        try (InputStream inputStream = response.getBodyStream()) {
            byte[] content = new byte[14];
            int length = inputStream.read(content);
            assertEquals(14, length);
            assertEquals("binary-content", new String(content, StandardCharsets.UTF_8));
        }
    }

    /**
     * 204 响应没有响应体，STREAM 模式下应返回 null 流而不是抛异常，与 OkHttp3 适配层行为一致。
     */
    @Test
    void shouldHandleStreamResponseWithoutBody() {
        server.enqueue(new MockResponse().setResponseCode(204));

        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.GET)
                .url(server.url("/webapi/entry.cgi").toString())
                .responseBodyMode(ResponseBodyMode.STREAM)
                .build();

        SynologyHttpResponse response = httpClient.execute(request);

        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
        assertNull(response.getBodyStream());
    }

    /**
     * 连接失败等底层异常应统一包装为 SynologyHttpException。
     */
    @Test
    void shouldWrapConnectionFailureAsSynologyHttpException() {
        SynologyHttpRequest request = SynologyHttpRequest.builder()
                .method(SynologyHttpMethod.GET)
                .url("http://127.0.0.1:1/webapi/entry.cgi")
                .readTimeoutMillis(2000)
                .build();

        assertThrows(SynologyHttpException.class, () -> httpClient.execute(request));
    }

    /**
     * 解析 x-www-form-urlencoded 表单体，兼容 + 和 %20 两种空格编码。
     */
    private Map<String, String> parseForm(String body) throws IOException {
        Map<String, String> form = new LinkedHashMap<String, String>();
        for (String pair : body.split("&")) {
            int separator = pair.indexOf('=');
            String name = URLDecoder.decode(pair.substring(0, separator), StandardCharsets.UTF_8.name());
            String value = URLDecoder.decode(pair.substring(separator + 1), StandardCharsets.UTF_8.name());
            form.put(name, value);
        }
        return form;
    }
}
