package io.github.shuzhuoi.synology.filestation.favorite;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 收藏夹列表响应。
 */
public class FavoriteListResponse {

    private Integer total;
    private Integer offset;
    private List<Favorite> favorites = new ArrayList<Favorite>();

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

    public List<Favorite> getFavorites() {
        return Collections.unmodifiableList(favorites);
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites == null ? new ArrayList<Favorite>() : new ArrayList<Favorite>(favorites);
    }
}
