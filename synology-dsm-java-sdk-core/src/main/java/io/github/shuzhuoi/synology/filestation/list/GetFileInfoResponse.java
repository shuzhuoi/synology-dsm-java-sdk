package io.github.shuzhuoi.synology.filestation.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.shuzhuoi.synology.filestation.model.SynologyFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFileInfoResponse {

    private List<SynologyFile> files = new ArrayList<SynologyFile>();

    public List<SynologyFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public void setFiles(List<SynologyFile> files) {
        this.files = files == null ? new ArrayList<SynologyFile>() : new ArrayList<SynologyFile>(files);
    }
}
