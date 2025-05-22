package com.antgroup.openspgapp.server.api.facade.dto;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/Page.class */
public class Page<T> {
  Long total;
  Long pageSize;
  Long pageNo;
  T data;

  public void setTotal(final Long total) {
    this.total = total;
  }

  public void setPageSize(final Long pageSize) {
    this.pageSize = pageSize;
  }

  public void setPageNo(final Long pageNo) {
    this.pageNo = pageNo;
  }

  public void setData(final T data) {
    this.data = data;
  }

  public Long getTotal() {
    return this.total;
  }

  public Long getPageSize() {
    return this.pageSize;
  }

  public Long getPageNo() {
    return this.pageNo;
  }

  public T getData() {
    return this.data;
  }

  public Page() {}

  public Page(Long pageSize, Long pageNo) {
    this.pageSize = pageSize;
    this.pageNo = pageNo;
  }
}
