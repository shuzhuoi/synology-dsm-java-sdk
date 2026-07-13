package io.github.shuzhuoi.synology.filestation.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyPermission {

    private Boolean read;
    private Boolean write;
    private Boolean execute;

    @SynologyJsonProperty("acl_enable")
    private Boolean aclEnable;

    @SynologyJsonProperty("is_acl_mode")
    private Boolean aclMode;
}
