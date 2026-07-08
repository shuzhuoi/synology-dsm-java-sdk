package io.github.shuzhuoi.synology.filestation.list;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.List 客户端。
 * <p>
 * 覆盖共享目录列表、目录文件列表和文件详情三个高频能力。
 */
public class FileStationListClient {

    private final SynologyApiExecutor executor;

    public FileStationListClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public ListSharesResponse shares() {
        return shares(ListSharesRequest.builder().build());
    }

    public ListSharesResponse shares(ListSharesRequest request) {
        // list_share 用于列出当前用户可访问的共享目录。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("onlywritable", SynologyParameterEncoder.booleanValue(request.getOnlyWritable()));
        parameters.put("additional", SynologyParameterEncoder.additionalList(request.getAdditional()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.List", 2, "list_share", parameters, ListSharesResponse.class);
    }

    public ListFilesResponse files(String folderPath) {
        return files(ListFilesRequest.builder(folderPath).build());
    }

    public ListFilesResponse files(ListFilesRequest request) {
        // list 用于枚举指定共享目录或子目录下的文件。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("folder_path", SynologyParameterEncoder.quoted(request.getFolderPath()));
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("pattern", request.getPattern());
        parameters.put("filetype", request.getFiletype());
        parameters.put("goto_path", request.getGotoPath());
        parameters.put("additional", SynologyParameterEncoder.additionalList(request.getAdditional()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.List", 2, "list", parameters, ListFilesResponse.class);
    }

    public GetFileInfoResponse info(GetFileInfoRequest request) {
        // getinfo 支持一次查询多个文件/文件夹路径。
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        parameters.put("additional", SynologyParameterEncoder.additionalList(request.getAdditional()));
        return executor.getAuthenticated("entry.cgi", "SYNO.FileStation.List", 2, "getinfo", parameters, GetFileInfoResponse.class);
    }
}
