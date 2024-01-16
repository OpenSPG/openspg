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

package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfigs;
import com.antgroup.openspg.builder.model.record.*;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class SPGTypeMappingProcessor extends BaseProcessor<SPGTypeMappingNodeConfigs> {

  private final List<SPGTypeMappingHelper> mappingHelpers;

  public SPGTypeMappingProcessor(String id, String name, SPGTypeMappingNodeConfigs config) {
    super(id, name, config);
    this.mappingHelpers =
        config.getMappingNodeConfigs().stream()
            .map(SPGTypeMappingHelper::new)
            .collect(Collectors.toList());
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    this.mappingHelpers.forEach(x -> x.init(context));
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseSPGRecord> resultSpgRecords = new ArrayList<>(inputs.size());

    List<BuilderRecord> emptyIdentifierRecords = new ArrayList<>(inputs.size());
    Map<SPGTypeIdentifier, List<BuilderRecord>> identifierRecords =
        new HashMap<>(mappingHelpers.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (record.getIdentifier() == null) {
        emptyIdentifierRecords.add(record);
      } else {
        List<BuilderRecord> existedRecords =
            identifierRecords.computeIfAbsent(record.getIdentifier(), k -> new ArrayList<>());
        existedRecords.add(record);
      }
    }

    for (SPGTypeMappingHelper mappingHelper : mappingHelpers) {
      for (BuilderRecord record : emptyIdentifierRecords) {
        resultSpgRecords.addAll(toSPGRecords(mappingHelper, record));
      }
    }

    for (SPGTypeMappingHelper mappingHelper : mappingHelpers) {
      List<BuilderRecord> identifiedRecords = identifierRecords.get(mappingHelper.getIdentifier());
      if (CollectionUtils.isEmpty(identifiedRecords)) {
        continue;
      }

      for (BuilderRecord record : identifiedRecords) {
        resultSpgRecords.addAll(toSPGRecords(mappingHelper, record));
      }
    }
    return (List) resultSpgRecords;
  }

  @Override
  public void close() throws Exception {}

  private List<BaseSPGRecord> toSPGRecords(
      SPGTypeMappingHelper mappingHelper, BuilderRecord record) {
    if (mappingHelper.isFiltered(record)) {
      return Collections.emptyList();
    }

    return mappingHelper.toSPGRecords(record);
  }
}
