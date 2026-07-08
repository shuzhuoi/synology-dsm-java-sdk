package io.github.shuzhuoi.synology.filestation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynologyPermission {

    private Boolean read;
    private Boolean write;
    private Boolean execute;

    @JsonProperty("acl_enable")
    private Boolean aclEnable;

    @JsonProperty("is_acl_mode")
    private Boolean aclMode;
}
