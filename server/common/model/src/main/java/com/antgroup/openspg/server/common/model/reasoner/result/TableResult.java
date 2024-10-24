/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
package com.antgroup.openspg.server.common.model.reasoner.result;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.List;

public class TableResult extends BaseModel {
  private static final long serialVersionUID = 6983069694645225535L;

  private long total;
  private String[] header;
  private List<Object[]> rows;

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public String[] getHeader() {
    return header;
  }

  public void setHeader(String[] header) {
    this.header = header;
  }

  public List<Object[]> getRows() {
    return rows;
  }

  public void setRows(List<Object[]> rows) {
    this.rows = rows;
  }
}
