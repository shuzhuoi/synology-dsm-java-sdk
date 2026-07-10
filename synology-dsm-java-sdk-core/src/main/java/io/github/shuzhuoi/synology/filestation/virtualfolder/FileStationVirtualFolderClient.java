package io.github.shuzhuoi.synology.filestation.virtualfolder;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.VirtualFolder 客户端。
 */
public class FileStationVirtualFolderClient {

    private static final String API_NAME = "SYNO.FileStation.VirtualFolder";
    private static final int API_VERSION = 2;

    private final SynologyApiExecutor executor;

    public FileStationVirtualFolderClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public VirtualFolderListResponse list(VirtualFolderListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("type", SynologyParameterEncoder.quoted(request.getType()));
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("additional", SynologyParameterEncoder.additionalList(request.getAdditional()));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "list", parameters, VirtualFolderListResponse.class);
    }
}
