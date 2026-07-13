package io.github.shuzhuoi.synology.filestation.file;

import io.github.shuzhuoi.synology.filestation.model.SynologyFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateFolderResponse {

    private List<SynologyFile> folders = new ArrayList<SynologyFile>();

    public List<SynologyFile> getFolders() {
        return Collections.unmodifiableList(folders);
    }

    public void setFolders(List<SynologyFile> folders) {
        this.folders = folders == null ? new ArrayList<SynologyFile>() : new ArrayList<SynologyFile>(folders);
    }
}
