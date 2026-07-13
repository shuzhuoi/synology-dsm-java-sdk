package io.github.shuzhuoi.synology.json.fastjson2;

import io.github.shuzhuoi.synology.api.SynologyApiDescriptor;
import io.github.shuzhuoi.synology.exception.SynologyJsonException;
import io.github.shuzhuoi.synology.filestation.backgroundtask.BackgroundTask;
import io.github.shuzhuoi.synology.filestation.extract.ArchiveItem;
import io.github.shuzhuoi.synology.filestation.extract.ExtractListResponse;
import io.github.shuzhuoi.synology.filestation.info.FileStationInfoResponse;
import io.github.shuzhuoi.synology.json.SynologyJsonResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Fastjson2 Codec 契约测试，使用与 Jackson 模块对等的 DSM 响应场景。
 */
class Fastjson2SynologyJsonCodecTest {

    private final Fastjson2SynologyJsonCodec codec = new Fastjson2SynologyJsonCodec();

    @Test
    void decodesEntityAndIgnoresUnknownFields() {
        SynologyJsonResponse<SynologyApiDescriptor> response = codec.decode(
                "{\"success\":true,\"data\":{\"path\":\"entry.cgi\",\"minVersion\":1,\"unknown\":true}}",
                SynologyApiDescriptor.class
        );

        assertEquals("entry.cgi", response.getData().getPath());
        assertEquals(Integer.valueOf(1), response.getData().getMinVersion());
    }

    @Test
    void decodesMapWithLinkedHashMapSemantics() {
        SynologyJsonResponse<Map<String, SynologyApiDescriptor>> response = codec.decodeMap(
                "{\"success\":true,\"data\":{\"first\":{\"path\":\"a\"},\"second\":{\"path\":\"b\"}}}",
                SynologyApiDescriptor.class
        );

        assertEquals("first", response.getData().keySet().iterator().next());
        assertEquals("b", response.getData().get("second").getPath());
    }

    @Test
    void decodesUnderscoreFieldsAndStringOrArrayList() {
        SynologyJsonResponse<FileStationInfoResponse> stringResponse = codec.decode(
                "{\"success\":true,\"data\":{\"is_manager\":true,\"support_virtual_protocol\":\"cifs, iso\",\"support_sharing\":false}}",
                FileStationInfoResponse.class
        );
        SynologyJsonResponse<FileStationInfoResponse> arrayResponse = codec.decode(
                "{\"success\":true,\"data\":{\"support_virtual_protocol\":[\"cifs\",\"iso\"]}}",
                FileStationInfoResponse.class
        );

        assertEquals(Boolean.TRUE, stringResponse.getData().getManager());
        assertEquals(Boolean.FALSE, stringResponse.getData().getSupportSharing());
        assertEquals(java.util.Arrays.asList("cifs", "iso"), stringResponse.getData().getSupportVirtualProtocol());
        assertEquals(java.util.Arrays.asList("cifs", "iso"), arrayResponse.getData().getSupportVirtualProtocol());
    }

    @Test
    void decodesFieldAliasAndDynamicBackgroundTaskParams() {
        SynologyJsonResponse<ArchiveItem> archiveResponse = codec.decode(
                "{\"success\":true,\"data\":{\"itemid\":1,\"pack_size\":12,\"is_dir\":false}}",
                ArchiveItem.class
        );
        SynologyJsonResponse<BackgroundTask> taskResponse = codec.decode(
                "{\"success\":true,\"data\":{\"params\":{\"path\":[\"/home\"],\"enabled\":true,\"count\":2}}}",
                BackgroundTask.class
        );

        assertEquals(Integer.valueOf(1), archiveResponse.getData().getItemId());
        assertEquals(java.util.Arrays.asList("/home"), taskResponse.getData().getParams().get("path"));
        assertEquals(Boolean.TRUE, taskResponse.getData().getParams().get("enabled"));
        assertEquals(Integer.valueOf(2), taskResponse.getData().getParams().get("count"));
    }

    @Test
    void decodesAnnotationsInNestedListItems() {
        SynologyJsonResponse<ExtractListResponse> response = codec.decode(
                "{\"success\":true,\"data\":{\"items\":[{\"item_id\":2,\"pack_size\":12,\"is_dir\":true}]}}",
                ExtractListResponse.class
        );

        assertEquals(Integer.valueOf(2), response.getData().getItems().get(0).getItemId());
        assertEquals(Long.valueOf(12), response.getData().getItems().get(0).getPackSize());
        assertEquals(Boolean.TRUE, response.getData().getItems().get(0).getDir());
    }

    @Test
    void preservesErrorCodeAndNullDataSemantics() {
        SynologyJsonResponse<BackgroundTask> error = codec.decode(
                "{\"success\":false,\"error\":{\"code\":119}}",
                BackgroundTask.class
        );
        SynologyJsonResponse<BackgroundTask> empty = codec.decode(
                "{\"success\":true}",
                BackgroundTask.class
        );

        assertFalse(error.isSuccess());
        assertEquals(Integer.valueOf(119), error.getError().getCode());
        assertNull(error.getData());
        assertNull(empty.getData());
    }

    @Test
    void wrapsMalformedJsonAsSdkException() {
        assertThrows(SynologyJsonException.class, () -> codec.decode("{", BackgroundTask.class));
    }
}
