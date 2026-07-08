package io.github.shuzhuoi.synology.filestation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynologyOwner {

    private String user;
    private String group;
    private Integer uid;
    private Integer gid;
}
