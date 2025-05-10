package com.antgroup.openspgapp.core.reasoner.model.task.result;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.List;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/task/result/TableResult.class */
public class TableResult extends BaseModel {
  private static final long serialVersionUID = 6983069694645225535L;
  private long total;
  private String[] header;
  private List<Object[]> rows;

  public long getTotal() {
    return this.total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public String[] getHeader() {
    return this.header;
  }

  public void setHeader(String[] header) {
    this.header = header;
  }

  public List<Object[]> getRows() {
    return this.rows;
  }

  public void setRows(List<Object[]> rows) {
    this.rows = rows;
  }
}
