package com.antgroup.openspg.server.api.facade;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class Paged<T> implements Serializable {

  private List<T> results;

  private Integer pageIdx;

  private Integer pageSize;

  private Long total;

  public Long totalPageNum() {
    return (total / pageSize) + ((total % pageSize == 0) ? 0 : 1);
  }
}
