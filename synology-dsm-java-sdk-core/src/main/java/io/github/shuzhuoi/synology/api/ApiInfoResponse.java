package io.github.shuzhuoi.synology.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiInfoResponse {

    private final Map<String, SynologyApiDescriptor> apiDescriptors;

    public ApiInfoResponse(Map<String, SynologyApiDescriptor> apiDescriptors) {
        this.apiDescriptors = apiDescriptors == null
                ? Collections.<String, SynologyApiDescriptor>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, SynologyApiDescriptor>(apiDescriptors));
    }

    public Map<String, SynologyApiDescriptor> getApiDescriptors() {
        return apiDescriptors;
    }

    public SynologyApiDescriptor getApiDescriptor(String apiName) {
        return apiDescriptors.get(apiName);
    }
}
