package com.youxuan.common.api;

import java.util.Collections;
import java.util.List;

/**
 * 统一分页响应结果封装类
 * <p>
 * 所有需要分页查询的接口统一使用此类型返回，前端可直接解析该结构进行分页展示。
 * </p>
 *
 * @param <T> 列表中元素的数据类型
 */
public final class PageResponse<T> {

    /** 当前页码，从 1 开始 */
    private final int pageNo;
    /** 每页记录数 */
    private final int pageSize;
    /** 符合查询条件的总记录数 */
    private final long total;
    /** 当前页的数据列表 */
    private final List<T> list;

    /**
     * 构造分页响应对象
     * <p>如果传入的 list 为 null，自动转换为空列表，避免前端解析 NPE。</p>
     *
     * @param pageNo   当前页码
     * @param pageSize 每页记录数
     * @param total    总记录数
     * @param list     当前页数据列表（允许 null，内部会转为空列表）
     */
    public PageResponse(int pageNo, int pageSize, long total, List<T> list) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list == null ? Collections.emptyList() : list;
    }

    /** @return 当前页码 */
    public int getPageNo() {
        return pageNo;
    }

    /** @return 每页记录数 */
    public int getPageSize() {
        return pageSize;
    }

    /** @return 总记录数 */
    public long getTotal() {
        return total;
    }

    /** @return 当前页数据列表（不会为 null） */
    public List<T> getList() {
        return list;
    }
}
