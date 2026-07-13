package io.github.shuzhuoi.synology.filestation.model;

import io.github.shuzhuoi.synology.json.annotation.SynologyJsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyShareAdditional extends SynologyFileAdditional {

    @SynologyJsonProperty("volume_status")
    private SynologyVolumeStatus volumeStatus;
}
