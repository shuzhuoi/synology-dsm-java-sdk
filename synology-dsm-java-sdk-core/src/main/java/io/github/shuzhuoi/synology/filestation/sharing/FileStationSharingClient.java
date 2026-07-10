package io.github.shuzhuoi.synology.filestation.sharing;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.model.SynologyOperationResponse;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SYNO.FileStation.Sharing 客户端。
 * <p>
 * 用于创建、查询、编辑和删除 DSM 分享链接。
 */
public class FileStationSharingClient {

    private static final String API_NAME = "SYNO.FileStation.Sharing";
    private static final int API_VERSION = 3;

    private final SynologyApiExecutor executor;

    public FileStationSharingClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public SharingLink getInfo(String id) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", SynologyParameterEncoder.quoted(id));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "getinfo", parameters, SharingLink.class);
    }

    public SharingListResponse list() {
        return list(SharingListRequest.builder().build());
    }

    public SharingListResponse list(SharingListRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("offset", SynologyParameterEncoder.integerValue(request.getOffset()));
        parameters.put("limit", SynologyParameterEncoder.integerValue(request.getLimit()));
        parameters.put("sort_by", request.getSortBy());
        parameters.put("sort_direction", request.getSortDirection());
        parameters.put("force_clean", SynologyParameterEncoder.booleanValue(request.getForceClean()));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "list", parameters, SharingListResponse.class);
    }

    public SharingCreateResponse create(SharingCreateRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("path", SynologyParameterEncoder.quotedOrList(request.getPaths()));
        parameters.put("password", SynologyParameterEncoder.quoted(request.getPassword()));
        parameters.put("date_expired", SynologyParameterEncoder.quoted(request.getDateExpired()));
        parameters.put("date_available", SynologyParameterEncoder.quoted(request.getDateAvailable()));
        return executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "create", parameters, SharingCreateResponse.class);
    }

    public SynologyOperationResponse delete(String id) {
        return delete(Collections.singletonList(id));
    }

    public SynologyOperationResponse delete(List<String> ids) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", SynologyParameterEncoder.quotedOrList(ids));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "delete", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    public SynologyOperationResponse clearInvalid() {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "clear_invalid", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }

    public SynologyOperationResponse edit(SharingEditRequest request) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", SynologyParameterEncoder.quotedOrList(request.getIds()));
        parameters.put("password", SynologyParameterEncoder.quoted(request.getPassword()));
        parameters.put("date_expired", SynologyParameterEncoder.quoted(request.getDateExpired()));
        parameters.put("date_available", SynologyParameterEncoder.quoted(request.getDateAvailable()));
        executor.getAuthenticated("entry.cgi", API_NAME, API_VERSION, "edit", parameters, Object.class);
        return new SynologyOperationResponse(true);
    }
}
