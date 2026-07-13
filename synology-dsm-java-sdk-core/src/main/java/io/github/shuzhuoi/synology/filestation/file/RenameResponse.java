package io.github.shuzhuoi.synology.filestation.file;

import io.github.shuzhuoi.synology.filestation.model.SynologyFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenameResponse {

    private List<SynologyFile> files = new ArrayList<SynologyFile>();

    public List<SynologyFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public void setFiles(List<SynologyFile> files) {
        this.files = files == null ? new ArrayList<SynologyFile>() : new ArrayList<SynologyFile>(files);
    }
}
