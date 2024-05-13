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

package com.antgroup.openspg.server.api.facade.dto.schema.request;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

/** Query schema type. */
public class SPGTypeRequest extends BaseRequest {

  private static final long serialVersionUID = 7159265118550694524L;

  /** The unique name of entity to query. */
  private String name;

  private List<String> lstName;

  public String getName() {
    return name;
  }

  public List<String> getNameList() {
    if (CollectionUtils.isEmpty(lstName)) {
      lstName = Lists.newArrayList(name);
    }
    return lstName;
  }

  public SPGTypeRequest setName(String name) {
    this.name = name;
    return this;
  }

  public SPGTypeRequest() {}

  public SPGTypeRequest(String name) {
    this.name = name;
  }

  public SPGTypeRequest(List<String> lst) {
    this.lstName = lst;
  }

  @Override
  public String toString() {
    return name;
  }
}
