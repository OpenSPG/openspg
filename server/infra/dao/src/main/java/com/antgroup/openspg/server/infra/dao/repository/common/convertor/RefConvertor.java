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

import com.antgroup.openspg.server.common.model.ref.RefInfo;
import com.antgroup.openspg.server.infra.dao.dataobject.RefDO;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

public class RefConvertor {

  public static RefDO toDO(RefInfo refInfo) {
    RefDO refDO = new RefDO();
    refDO.setId(refInfo.getId());
    refDO.setGmtCreate(refInfo.getGmtCreate());
    refDO.setGmtModified(refInfo.getGmtModified());
    refDO.setName(refInfo.getName());
    refDO.setRefId(refInfo.getRefId());
    refDO.setRefedId(refInfo.getRefedId());
    refDO.setRefType(refInfo.getRefType());
    refDO.setRefedType(refInfo.getRefedType());
    refDO.setStatus(refInfo.getStatus());
    refDO.setConfig(refInfo.getConfig());
    return refDO;
  }

  public static RefInfo toModel(RefDO refDO) {
    if (null == refDO) {
      return null;
    }
    RefInfo refInfo = new RefInfo();
    refInfo.setId(refDO.getId());
    refInfo.setGmtCreate(refDO.getGmtCreate());
    refInfo.setGmtModified(refDO.getGmtModified());
    refInfo.setName(refDO.getName());
    refInfo.setRefId(refDO.getRefId());
    refInfo.setRefType(refDO.getRefType());
    refInfo.setRefedId(refDO.getRefedId());
    refInfo.setRefedType(refDO.getRefedType());
    refInfo.setStatus(refDO.getStatus());
    refInfo.setConfig(refDO.getConfig());
    return refInfo;
  }

  public static List<RefInfo> toModelList(List<RefDO> refDOList) {
    if (CollectionUtils.isEmpty(refDOList)) {
      return Lists.newArrayList();
    }
    return refDOList.stream().map(RefConvertor::toModel).collect(Collectors.toList());
  }
}
