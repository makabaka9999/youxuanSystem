package com.youxuan.order.dto;

import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * 批量选中/取消选中购物车项请求体
 * <p>
 * 用户在购物车页面批量操作商品选中状态时传入的参数。
 * 支持同时操作多个购物车项的选中状态。
 * </p>
 */
public class BatchCheckRequest {

    /** 购物车项ID列表 */
    @NotNull(message = "购物车项ID列表不能为空")
    private List<Long> itemIds;

    /** 是否选中：true-选中，false-取消选中 */
    @NotNull(message = "选中状态不能为空")
    private Boolean checked;

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
