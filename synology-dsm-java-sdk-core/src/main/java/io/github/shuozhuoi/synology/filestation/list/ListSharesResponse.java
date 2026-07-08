package io.github.shuzhuoi.synology.filestation.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.shuzhuoi.synology.filestation.model.SynologyShare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListSharesResponse {

    private Integer offset;
    private Integer total;
    private List<SynologyShare> shares = new ArrayList<SynologyShare>();

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

    public List<SynologyShare> getShares() {
        return Collections.unmodifiableList(shares);
    }

    public void setShares(List<SynologyShare> shares) {
        this.shares = shares == null ? new ArrayList<SynologyShare>() : new ArrayList<SynologyShare>(shares);
    }
}
