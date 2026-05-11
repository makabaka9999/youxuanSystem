package com.youxuan.platform.common.web;

import java.util.Collections;
import java.util.List;

public class PageResponse<T> {

    private final int pageNo;
    private final int pageSize;
    private final long total;
    private final List<T> list;

    public PageResponse(int pageNo, int pageSize, long total, List<T> list) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list == null ? Collections.emptyList() : list;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public List<T> getList() {
        return list;
    }
}
