package io.github.shuzhuoi.synology.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynologyApiDescriptor {

    private String path;

    @JsonProperty("minVersion")
    private Integer minVersion;

    @JsonProperty("maxVersion")
    private Integer maxVersion;

    private String requestFormat;
}
