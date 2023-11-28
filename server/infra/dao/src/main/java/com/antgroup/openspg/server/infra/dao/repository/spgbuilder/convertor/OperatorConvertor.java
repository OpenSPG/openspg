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

package com.antgroup.openspg.server.infra.dao.repository.spgbuilder.convertor;

import com.antgroup.openspg.server.infra.dao.dataobject.OperatorOverviewDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OperatorVersionDO;
import com.antgroup.openspg.common.model.LangTypeEnum;
import com.antgroup.openspg.builder.core.operator.OperatorOverview;
import com.antgroup.openspg.builder.core.operator.OperatorVersion;
import com.antgroup.openspg.schema.model.type.OperatorTypeEnum;

public class OperatorConvertor {

  public static OperatorOverview toModel(OperatorOverviewDO operatorOverviewDO) {
    if (operatorOverviewDO == null) {
      return null;
    }
    return new OperatorOverview(
        operatorOverviewDO.getId(),
        operatorOverviewDO.getName(),
        operatorOverviewDO.getDescription(),
        OperatorTypeEnum.valueOf(operatorOverviewDO.getType()),
        LangTypeEnum.valueOf(operatorOverviewDO.getLang()));
  }

  public static OperatorOverviewDO toDO(OperatorOverview operatorOverview) {
    OperatorOverviewDO operatorOverviewDO = new OperatorOverviewDO();
    operatorOverviewDO.setName(operatorOverview.getName());
    operatorOverviewDO.setDescription(operatorOverview.getDesc());
    operatorOverviewDO.setType(operatorOverview.getType().name());
    operatorOverviewDO.setLang(operatorOverview.getLangType().name());
    return operatorOverviewDO;
  }

  public static OperatorVersion toModel(OperatorVersionDO operatorVersionDO) {
    if (operatorVersionDO == null) {
      return null;
    }

    return new OperatorVersion(
        operatorVersionDO.getOverviewId(),
        operatorVersionDO.getMainClass(),
        operatorVersionDO.getJarAddress(),
        operatorVersionDO.getVersion());
  }

  public static OperatorVersionDO toDO(OperatorVersion operatorVersion) {
    OperatorVersionDO operatorVersionDO = new OperatorVersionDO();

    operatorVersionDO.setOverviewId(operatorVersion.getOverviewId());
    operatorVersionDO.setMainClass(operatorVersion.getMainClass());
    operatorVersionDO.setJarAddress(operatorVersion.getFilePath());
    operatorVersionDO.setVersion(operatorVersion.getVersion());
    return operatorVersionDO;
  }
}
