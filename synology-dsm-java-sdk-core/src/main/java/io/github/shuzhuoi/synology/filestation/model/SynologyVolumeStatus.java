package io.github.shuzhuoi.synology.filestation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyVolumeStatus {

    private String freespace;
    private String totalspace;
    private Boolean readonly;
}
