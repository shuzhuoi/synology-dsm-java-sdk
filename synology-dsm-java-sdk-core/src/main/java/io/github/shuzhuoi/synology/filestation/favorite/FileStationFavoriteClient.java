package io.github.shuzhuoi.synology.filestation.favorite;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;
import io.github.shuzhuoi.synology.util.SynologyPath;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SYNO.FileStation.Favorite 客户端。
 */
public class FileStationFavoriteClient {

    private static final String API_NAME = "SYNO.FileStation.Favorite";
    private static final int API_VERSION = 2;

    private final SynologyApiExecutor executor;

    public FileStationFavoriteClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public FavoriteListResponse list() {
        return list(FavoriteListRequest.builder().build());
    }

    public FavoriteListResponse list(FavoriteListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("status_filter", request.getStatusFilter());
        parameters.put("additional", SynologyParameterEncoder.additionalList(request.getAdditional()));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "list", parameters, FavoriteListResponse.class);
    }

    public SynologyOperationResponse add(FavoriteAddRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.quoted(request.getPath()));
        parameters.put("name", SynologyParameterEncoder.quoted(request.getName()));
        parameters.put("index", SynologyParameterEncoder.integerValue(request.getIndex()));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "add", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    public SynologyOperationResponse delete(String path) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.quoted(SynologyPath.normalize(path)));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "delete", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    public SynologyOperationResponse clearBroken() {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "clear_broken", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    public SynologyOperationResponse edit(FavoriteEditRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.quoted(request.getPath()));
        parameters.put("name", SynologyParameterEncoder.quoted(request.getName()));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "edit", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    public SynologyOperationResponse replaceAll(FavoriteReplaceAllRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.stringList(request.getPaths()));
        parameters.put("name", SynologyParameterEncoder.stringList(request.getNames()));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "replace_all", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }
}
