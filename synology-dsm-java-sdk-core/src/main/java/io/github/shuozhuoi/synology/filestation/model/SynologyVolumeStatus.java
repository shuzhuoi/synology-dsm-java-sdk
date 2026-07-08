package io.github.shuzhuoi.synology.filestation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynologyVolumeStatus {

    private String freespace;
    private String totalspace;
    private Boolean readonly;
}
