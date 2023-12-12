/*
 * Copyright 2023 Ant Group CO., Ltd.
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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.model.query;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * Scheduler Job Query Model
 * @version : SchedulerJobQuery.java, v 0.1 2023-12-04 11:23 $
 */
public class SchedulerJobQuery extends SchedulerJob {

  private static final long serialVersionUID = -857200975331899039L;

  /** page No */
  private Integer pageNo;
  /** page Size */
  private Integer pageSize;
  /** sort */
  private String sort;
  /** order asc, desc */
  private String order;

  /** keyword, like query name/id/createUserName */
  private String keyword;
  /** types */
  private List<String> types = Lists.newArrayList();

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public List<String> getTypes() {
    return types;
  }

  public void setTypes(List<String> types) {
    this.types = types;
  }
}
