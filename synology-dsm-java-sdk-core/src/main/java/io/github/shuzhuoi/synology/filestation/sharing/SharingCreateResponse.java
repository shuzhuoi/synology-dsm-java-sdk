package io.github.shuzhuoi.synology.filestation.sharing;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 创建分享链接响应。
 */
public class SharingCreateResponse {

    private List<SharingLink> links = new ArrayList<SharingLink>();

    public List<SharingLink> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public void setLinks(List<SharingLink> links) {
        this.links = links == null ? new ArrayList<SharingLink>() : new ArrayList<SharingLink>(links);
    }
}
