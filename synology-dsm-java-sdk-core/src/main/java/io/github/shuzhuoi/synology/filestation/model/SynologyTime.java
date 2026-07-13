package io.github.shuzhuoi.synology.filestation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SynologyTime {

    private Long atime;
    private Long mtime;
    private Long ctime;
    private Long crtime;
}
