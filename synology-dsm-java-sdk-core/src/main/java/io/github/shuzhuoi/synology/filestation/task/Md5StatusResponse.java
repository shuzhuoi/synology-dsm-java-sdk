package io.github.shuzhuoi.synology.filestation.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Md5StatusResponse {

    private Boolean finished;
    private String md5;
    private String status;
}
