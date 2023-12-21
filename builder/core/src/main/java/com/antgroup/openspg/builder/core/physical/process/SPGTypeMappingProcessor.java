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

package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.operator.fusing.SubjectFusing;
import com.antgroup.openspg.builder.core.operator.fusing.SubjectFusingImpl;
import com.antgroup.openspg.builder.core.operator.linking.RecordLinking;
import com.antgroup.openspg.builder.core.operator.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.core.operator.linking.impl.SearchBasedLinking;
import com.antgroup.openspg.builder.core.operator.predicating.RecordPredicting;
import com.antgroup.openspg.builder.core.operator.predicating.RecordPredictingImpl;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SPGTypeMappingProcessor extends BaseMappingProcessor<SPGTypeMappingNodeConfig> {

  private BaseSPGType spgType;
  private RecordLinking recordLinking;
  private RecordPredicting recordPredicating;
  private SubjectFusing subjectFusing;

  public SPGTypeMappingProcessor(String id, String name, SPGTypeMappingNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);

    SPGTypeIdentifier identifier = SPGTypeIdentifier.parse(config.getSpgType());
    this.spgType = (BaseSPGType) loadSchema(identifier, context.getCatalog());

    this.recordLinking = new RecordLinkingImpl(config.getMappingConfigs());
    this.recordLinking.setDefaultPropertyLinking(new SearchBasedLinking());
    this.recordLinking.init(context);

    this.subjectFusing = new SubjectFusingImpl(config.getSubjectFusingConfig());
    this.subjectFusing.init(context);

    this.recordPredicating = new RecordPredictingImpl(config.getPredictingConfigs());
    this.recordPredicating.init(context);
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseAdvancedRecord> advancedRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (isFiltered(record, config.getMappingFilters())) {
        continue;
      }

      BuilderRecord mappedRecord = mapping(record, config.getMappingConfigs());
      BaseAdvancedRecord advancedRecord = toSPGRecord(mappedRecord, spgType);
      if (advancedRecord != null) {
        recordLinking.propertyLinking(advancedRecord);
        recordPredicating.propertyPredicating(advancedRecord);
        advancedRecords.add(advancedRecord);
      }
    }
    return subjectFusing.subjectFusing(advancedRecords);
  }

  @Override
  public void close() throws Exception {}
}
