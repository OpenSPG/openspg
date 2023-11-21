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

package com.antgroup.openspg.api.facade.dto.builder.response;

import com.antgroup.openspg.common.model.base.BaseResponse;

public class BaseEditResponse extends BaseResponse {

  private Integer updated;

  public Integer getUpdated() {
    return updated;
  }

  public BaseEditResponse setUpdated(Integer updated) {
    this.updated = updated;
    return this;
  }
}
