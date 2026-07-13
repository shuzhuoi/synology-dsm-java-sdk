package io.github.shuzhuoi.synology.api;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyApiDescriptor {

    private String path;

    @SynologyJsonProperty("minVersion")
    private Integer minVersion;

    @SynologyJsonProperty("maxVersion")
    private Integer maxVersion;

    private String requestFormat;
}
