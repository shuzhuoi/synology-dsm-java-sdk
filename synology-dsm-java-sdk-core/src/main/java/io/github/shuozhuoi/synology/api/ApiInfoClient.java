package io.github.shuzhuoi.synology.api;

import io.github.shuzhuoi.synology.internal.SynologyApiExecutor;
import io.github.shuzhuoi.synology.util.SynologyParameterEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiInfoClient {

    private final SynologyApiExecutor executor;

    public ApiInfoClient(SynologyApiExecutor executor) {
        this.executor = executor;
    }

    public ApiInfoResponse queryAll() {
        return query("all");
    }

    public ApiInfoResponse query(String query) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("query", SynologyParameterEncoder.stringValue(query));
        Map<String, SynologyApiDescriptor> descriptors = executor.getPublicMap(
                "query.cgi",
                "SYNO.API.Info",
                1,
                "query",
                parameters,
                SynologyApiDescriptor.class
        );
        return new ApiInfoResponse(descriptors);
    }
}
