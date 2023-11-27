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

package com.antgroup.openspg.server.core.builder.service.impl;

import com.antgroup.openspg.core.spgbuilder.model.operator.OperatorOverview;
import com.antgroup.openspg.core.spgbuilder.model.operator.OperatorVersion;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.core.spgbuilder.service.OperatorService;
import com.antgroup.openspg.core.spgbuilder.service.impl.convertor.OperatorConvertor;
import com.antgroup.openspg.core.spgbuilder.service.repo.OperatorRepository;
import com.antgroup.openspg.core.spgschema.model.type.OperatorKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperatorServiceImpl implements OperatorService {

  @Autowired private OperatorRepository operatorRepository;

  @Override
  public List<OperatorConfig> query(Collection<OperatorKey> keys) {
    List<OperatorConfig> operatorConfigs = new ArrayList<>(keys.size());
    if (keys.isEmpty()) {
      return operatorConfigs;
    }
    Map<String, List<OperatorKey>> operatorName2Keys =
        keys.stream().collect(Collectors.groupingBy(OperatorKey::getName));
    Map<String, OperatorOverview> operatorOverviews =
        operatorRepository.batchQuery(new ArrayList<>(operatorName2Keys.keySet())).stream()
            .collect(Collectors.toMap(OperatorOverview::getName, Function.identity()));

    for (Map.Entry<String, List<OperatorKey>> entry : operatorName2Keys.entrySet()) {
      String operatorName = entry.getKey();
      List<OperatorKey> operatorKeys = entry.getValue();
      OperatorOverview overview = operatorOverviews.get(operatorName);
      if (overview == null) {
        continue;
      }

      List<Integer> versionIds =
          operatorKeys.stream()
              .map(OperatorKey::getVersion)
              .distinct()
              .collect(Collectors.toList());
      List<OperatorVersion> versions = operatorRepository.batchQuery(overview.getId(), versionIds);
      for (OperatorVersion version : versions) {
        operatorConfigs.add(OperatorConvertor.toOperatorConfig(overview, version));
      }
    }
    return operatorConfigs;
  }
}
