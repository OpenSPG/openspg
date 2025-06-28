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
package com.antgroup.openspg.server.common.model.provider;

import com.alibaba.fastjson.JSONArray;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class ModelProvider extends BaseModel {

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String modifier;
  private String creator;
  private String name;
  private String provider;
  private String status;
  private String pageMode;
  private List<String> modelType;
  private String logo;
  private String tags;
  private JSONArray params;

  public static ModelProvider modelProvider(ModelProvider provider) {
    ModelProvider modelProvider = new ModelProvider();
    modelProvider.setName(provider.getName());
    modelProvider.setProvider(provider.getProvider());
    modelProvider.setPageMode(provider.getPageMode());
    modelProvider.setModelType(provider.getModelType());
    modelProvider.setLogo(provider.getLogo());
    modelProvider.setTags(provider.getTags());
    modelProvider.setParams(provider.getParams());
    return modelProvider;
  }
}
