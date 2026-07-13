package io.github.shuzhuoi.synology.filestation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyOwner {

    private String user;
    private String group;
    private Integer uid;
    private Integer gid;
}
