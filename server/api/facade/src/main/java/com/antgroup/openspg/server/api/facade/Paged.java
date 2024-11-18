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
