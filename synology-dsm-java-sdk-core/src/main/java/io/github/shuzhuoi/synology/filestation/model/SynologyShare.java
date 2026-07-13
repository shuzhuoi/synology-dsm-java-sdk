package io.github.shuzhuoi.synology.filestation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyShare {

    private String name;
    private String path;
    private Boolean isdir;
    private SynologyShareAdditional additional;
}
