package io.github.shuzhuoi.synology.filestation.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadFileResponse {

    /**
     * 上传是否成功。官方上传接口成功时返回空 data，因此这里由 SDK 在调用成功后明确标记。
     */
    private final boolean success;
}
