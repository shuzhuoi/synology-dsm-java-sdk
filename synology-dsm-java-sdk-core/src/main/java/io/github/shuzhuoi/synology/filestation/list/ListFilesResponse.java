package io.github.shuzhuoi.synology.filestation.list;

import io.github.shuzhuoi.synology.filestation.model.SynologyFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFilesResponse {

    private Integer offset;
    private Integer total;
    private List<SynologyFile> files = new ArrayList<SynologyFile>();

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<SynologyFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public void setFiles(List<SynologyFile> files) {
        this.files = files == null ? new ArrayList<SynologyFile>() : new ArrayList<SynologyFile>(files);
    }
}
