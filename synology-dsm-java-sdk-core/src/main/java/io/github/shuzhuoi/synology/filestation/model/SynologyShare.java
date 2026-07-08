package io.github.shuzhuoi.synology.filestation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynologyShare {

    private String name;
    private String path;
    private Boolean isdir;
    private SynologyShareAdditional additional;
}
