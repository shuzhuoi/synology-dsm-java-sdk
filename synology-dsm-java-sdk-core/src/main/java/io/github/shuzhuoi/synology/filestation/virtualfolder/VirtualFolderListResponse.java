package io.github.shuzhuoi.synology.filestation.virtualfolder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 虚拟目录列表响应。
 */
public class VirtualFolderListResponse {

    private Integer total;
    private Integer offset;
    private List<VirtualFolder> folders = new ArrayList<VirtualFolder>();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<VirtualFolder> getFolders() {
        return Collections.unmodifiableList(folders);
    }

    public void setFolders(List<VirtualFolder> folders) {
        this.folders = folders == null ? new ArrayList<VirtualFolder>() : new ArrayList<VirtualFolder>(folders);
    }
}
