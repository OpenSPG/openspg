package com.antgroup.openspg.server.common.model.base;

/**
 * @author 庄舟
 * @Title: Page.java
 * @Description:
 */
public class Page<T> extends BaseToString {

    private static final long serialVersionUID = 7805112437553092273L;

    Long    total;
    Integer pageSize;
    Integer pageNo;
    T       data;

    public Page() {
    }

    public Page(Integer pageSize, Integer pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
