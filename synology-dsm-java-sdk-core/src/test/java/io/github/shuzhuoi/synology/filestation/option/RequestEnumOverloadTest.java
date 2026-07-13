package io.github.shuzhuoi.synology.filestation.option;

import io.github.shuzhuoi.synology.filestation.compress.CompressFormat;
import io.github.shuzhuoi.synology.filestation.compress.CompressLevel;
import io.github.shuzhuoi.synology.filestation.compress.CompressMode;
import io.github.shuzhuoi.synology.filestation.compress.CompressStartRequest;
import io.github.shuzhuoi.synology.filestation.list.ListFilesRequest;
import io.github.shuzhuoi.synology.filestation.thumb.ThumbGetRequest;
import io.github.shuzhuoi.synology.filestation.thumb.ThumbSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 enum 重载与原 String 方法生成相同的 DSM 参数值。
 */
class RequestEnumOverloadTest {

    @Test
    void listEnumValuesMatchStringValues() {
        ListFilesRequest enumRequest = ListFilesRequest.builder("/home")
                .sortDirection(SortDirection.ASC)
                .filetype(FileTypeFilter.FILE)
                .build();
        ListFilesRequest stringRequest = ListFilesRequest.builder("/home")
                .sortDirection("asc")
                .filetype("file")
                .build();

        assertEquals(stringRequest.getSortDirection(), enumRequest.getSortDirection());
        assertEquals(stringRequest.getFiletype(), enumRequest.getFiletype());
    }

    @Test
    void archiveAndThumbEnumValuesMatchStringValues() {
        CompressStartRequest compressRequest = CompressStartRequest.builder("/home/a", "/home/a.zip")
                .level(CompressLevel.MODERATE)
                .mode(CompressMode.ADD)
                .format(CompressFormat.ZIP)
                .build();
        ThumbGetRequest thumbRequest = ThumbGetRequest.builder("/home/a.jpg")
                .size(ThumbSize.SMALL)
                .build();

        assertEquals("moderate", compressRequest.getLevel());
        assertEquals("add", compressRequest.getMode());
        assertEquals("zip", compressRequest.getFormat());
        assertEquals("small", thumbRequest.getSize());
    }
}
