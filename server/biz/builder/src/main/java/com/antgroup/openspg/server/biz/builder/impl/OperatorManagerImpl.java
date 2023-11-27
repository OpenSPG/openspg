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

package com.antgroup.openspg.server.biz.builder.impl;

import com.antgroup.openspg.biz.spgbuilder.OperatorManager;
import com.antgroup.openspg.cloudext.interfaces.objectstore.ObjectStoreClient;
import com.antgroup.openspg.cloudext.interfaces.objectstore.cmd.ObjectStoreSaveCmd;
import com.antgroup.openspg.cloudext.interfaces.objectstore.model.ObjectStorePath;
import com.antgroup.openspg.server.common.service.datasource.DataSourceService;
import com.antgroup.openspg.core.spgbuilder.service.repo.OperatorRepository;
import com.antgroup.openspg.server.api.facade.dto.builder.request.OperatorCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.builder.request.OperatorVersionRequest;
import com.antgroup.openspg.server.api.facade.dto.builder.response.OperatorCreateResponse;
import com.antgroup.openspg.server.api.facade.dto.builder.response.OperatorVersionResponse;
import com.antgroup.openspg.server.common.model.LangTypeEnum;
import com.antgroup.openspg.server.core.builder.model.operator.OperatorOverview;
import com.antgroup.openspg.server.core.builder.model.operator.OperatorVersion;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperatorManagerImpl implements OperatorManager {

  @Autowired private OperatorRepository operatorRepository;

  @Autowired private DataSourceService dataSourceService;

  @Override
  public OperatorCreateResponse create(OperatorCreateRequest request) {
    operatorRepository.save(
        new OperatorOverview(
            null,
            request.getName(),
            request.getDesc(),
            request.getOperatorType(),
            LangTypeEnum.PYTHON));
    return new OperatorCreateResponse().setName(request.getName());
  }

  @Override
  public OperatorVersionResponse addVersion(OperatorVersionRequest request, InputStream file) {
    OperatorOverview operatorOverview = operatorRepository.query(request.getOperatorId());
    if (operatorOverview == null) {
      throw new IllegalArgumentException(
          String.format("Operator=%s not exist", request.getOperatorId()));
    }
    List<OperatorVersion> operatorVersions = operatorRepository.list(request.getOperatorId());
    Integer curVersion = 1;
    if (CollectionUtils.isNotEmpty(operatorVersions)) {
      curVersion = operatorVersions.get(0).getVersion() + 1;
    }

    ObjectStoreClient objectStoreClient = dataSourceService.buildSharedOperatorStoreClient();
    ObjectStorePath objectStorePath =
        objectStoreClient.save(
            new ObjectStoreSaveCmd(
                new ObjectStorePath(
                    String.format(
                        "%s_v%s.%s",
                        operatorOverview.getName(),
                        curVersion,
                        operatorOverview.getLangType().getSuffix())),
                file));
    operatorRepository.save(
        new OperatorVersion(
            request.getOperatorId(), "handle", objectStorePath.getRelativePath(), curVersion));
    return new OperatorVersionResponse()
        .setLatestVersion(curVersion)
        .setOperatorName(operatorOverview.getName());
  }

  @Override
  public List<OperatorOverview> listOverview(String name) {
    return operatorRepository.query(name);
  }

  @Override
  public List<OperatorVersion> listVersion(String name) {
    return operatorRepository.list(name);
  }
}
