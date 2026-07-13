package io.github.shuzhuoi.synology.filestation.sharing;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分享链接列表响应。
 */
public class SharingListResponse {

    private Integer total;
    private Integer offset;
    private List<SharingLink> links = new ArrayList<SharingLink>();

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

    public List<SharingLink> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public void setLinks(List<SharingLink> links) {
        this.links = links == null ? new ArrayList<SharingLink>() : new ArrayList<SharingLink>(links);
    }
}
