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
package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetail;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailDTO;
import com.antgroup.openspg.server.infra.dao.dataobject.ModelDetailDO;
import com.google.common.collect.Lists;
import java.util.List;

public class ModelDetailConvertor {

  public static ModelDetailDO toDO(ModelDetail modelDetail) {
    if (modelDetail == null) {
      return null;
    }
    ModelDetailDO modelDetailDO = new ModelDetailDO();
    modelDetailDO.setId(modelDetail.getId());
    modelDetailDO.setGmtCreate(modelDetail.getGmtCreate());
    modelDetailDO.setGmtModified(modelDetail.getGmtModified());
    modelDetailDO.setModifier(modelDetail.getModifier());
    modelDetailDO.setCreator(modelDetail.getCreator());
    modelDetailDO.setProvider(modelDetail.getProvider());
    modelDetailDO.setType(modelDetail.getType());
    modelDetailDO.setName(modelDetail.getName());
    modelDetailDO.setDescription(modelDetail.getDescription());
    if (modelDetail.getParams() != null) {
      String paramsStr = JSON.toJSONString(modelDetail.getParams());
      modelDetailDO.setParams(paramsStr);
    }
    return modelDetailDO;
  }

  public static ModelDetail toModel(ModelDetailDO modelDetailDO) {
    if (modelDetailDO == null) {
      return null;
    }
    ModelDetail modelDetail = new ModelDetail();
    modelDetail.setId(modelDetailDO.getId());
    modelDetail.setGmtCreate(modelDetailDO.getGmtCreate());
    modelDetail.setGmtModified(modelDetailDO.getGmtModified());
    modelDetail.setModifier(modelDetailDO.getModifier());
    modelDetail.setCreator(modelDetailDO.getCreator());
    modelDetail.setProvider(modelDetailDO.getProvider());
    modelDetail.setType(modelDetailDO.getType());
    modelDetail.setName(modelDetailDO.getName());
    modelDetail.setDescription(modelDetailDO.getDescription());
    if (StringUtils.isNotBlank(modelDetailDO.getParams())) {
      JSONObject parseArray = JSON.parseObject(modelDetailDO.getParams());
      modelDetail.setParams(parseArray);
    }
    return modelDetail;
  }

  public static ModelDetailDTO toDTO(ModelDetailDO modelDetailDO) {
    if (modelDetailDO == null) {
      return null;
    }
    ModelDetailDTO modelDetailDTO = new ModelDetailDTO();
    modelDetailDTO.setType(modelDetailDO.getType());
    modelDetailDTO.setName(modelDetailDO.getName());
    return modelDetailDTO;
  }

  public static List<ModelDetailDO> toDoList(List<ModelDetail> modelDetails) {
    if (modelDetails == null) {
      return null;
    }
    List<ModelDetailDO> dos = Lists.newArrayList();
    for (ModelDetail provider : modelDetails) {
      dos.add(toDO(provider));
    }
    return dos;
  }

  public static List<ModelDetail> toModelList(List<ModelDetailDO> modelDetailDOs) {
    if (modelDetailDOs == null) {
      return null;
    }
    List<ModelDetail> details = Lists.newArrayList();
    for (ModelDetailDO providerDO : modelDetailDOs) {
      details.add(toModel(providerDO));
    }
    return details;
  }

  public static List<ModelDetailDTO> toDTOList(List<ModelDetailDO> modelDetailDOs) {
    if (modelDetailDOs == null) {
      return null;
    }
    List<ModelDetailDTO> jobs = Lists.newArrayList();
    for (ModelDetailDO modelDetailDO : modelDetailDOs) {
      jobs.add(toDTO(modelDetailDO));
    }
    return jobs;
  }
}
