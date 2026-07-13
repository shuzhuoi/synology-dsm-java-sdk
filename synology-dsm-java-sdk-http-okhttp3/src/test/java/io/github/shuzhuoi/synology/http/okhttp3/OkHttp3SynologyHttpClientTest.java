package io.github.shuzhuoi.synology.http.okhttp3;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OkHttp3SynologyHttpClientTest {

    private MockWebServer server;
    private OkHttp3SynologyHttpClient httpClient;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        httpClient = new OkHttp3SynologyHttpClient();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

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

    @Test
    void shouldSendPostParametersAsFormBody() throws InterruptedException {
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
        assertEquals("api=SYNO.FileStation.Test&name=%E6%B5%8B%E8%AF%95%20file", recordedRequest.getBody().readUtf8());
    }

    @Test
    void shouldSendParametersAndFileAsMultipart(@TempDir Path tempDir) throws IOException, InterruptedException {
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
        assertTrue(requestBody.contains("SYNO.FileStation.Upload"));
        assertTrue(requestBody.contains("name=\"path\""));
        assertTrue(requestBody.contains("/target"));
        assertTrue(requestBody.contains("name=\"file\"; filename=\"upload.txt\""));
        assertTrue(requestBody.contains("file-content"));
    }

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
}
