package io.github.shuzhuoi.synology.filestation.permission;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.CheckPermission 客户端。
 * <p>
 * 用于在真正写入前检查当前登录用户是否具备目标目录与文件名的写入权限。
 */
public class FileStationCheckPermissionClient {

    private final SynologyApiExecutor executor;

    public FileStationCheckPermissionClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public CheckPermissionWriteResponse write(CheckPermissionWriteRequest request) {
        // 官方 write 成功时可能没有 data，仅以 success 表示通过；失败由统一执行器转换为异常。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.quoted(request.getPath()));
        parameters.put("filename", SynologyParameterEncoder.quoted(request.getFilename()));
        parameters.put("overwrite", SynologyParameterEncoder.booleanValue(request.getOverwrite()));
        parameters.put("create_only", SynologyParameterEncoder.booleanValue(request.getCreateOnly()));
        executor.getAuthenticated("entry.cgi", "SYNO.FileStation.CheckPermission", 3, "write", parameters, Object.class);
        return new CheckPermissionWriteResponse(true);
    }
}
