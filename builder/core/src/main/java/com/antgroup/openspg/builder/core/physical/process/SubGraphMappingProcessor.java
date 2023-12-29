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

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.fusing.SubGraphFusing;
import com.antgroup.openspg.builder.core.strategy.fusing.SubGraphFusingImpl;
import com.antgroup.openspg.builder.core.strategy.fusing.SubjectFusing;
import com.antgroup.openspg.builder.core.strategy.fusing.SubjectFusingImpl;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.core.strategy.linking.impl.IdEqualsLinking;
import com.antgroup.openspg.builder.core.strategy.predicting.RecordPredicting;
import com.antgroup.openspg.builder.core.strategy.predicting.RecordPredictingImpl;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.SubGraphMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class SubGraphMappingProcessor extends BaseMappingProcessor<SubGraphMappingNodeConfig> {

  private final SPGTypeIdentifier identifier;
  private BaseSPGType spgType;
  private SubGraphFusing subGraphFusing;
  private RecordPredicting recordPredicating;
  private SubjectFusing subjectFusing;
  private RecordLinking recordLinking;

  public SubGraphMappingProcessor(String id, String name, SubGraphMappingNodeConfig config) {
    super(id, name, config);
    this.identifier = SPGTypeIdentifier.parse(config.getSpgType());
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);

    this.spgType = (BaseSPGType) loadSchema(identifier, context.getCatalog());

    this.recordLinking = new RecordLinkingImpl();
    this.recordLinking.setDefaultPropertyLinking(IdEqualsLinking.INSTANCE);
    this.recordLinking.init(context);

    this.subGraphFusing = new SubGraphFusingImpl(config.getMappingConfigs(), recordLinking);
    this.subGraphFusing.init(context);

    this.subjectFusing = new SubjectFusingImpl(config.getSubjectFusingConfig());
    this.subjectFusing.init(context);

    this.recordPredicating = new RecordPredictingImpl(config.getPredictingConfigs());
    this.recordPredicating.init(context);
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseAdvancedRecord> advancedRecords = new ArrayList<>(inputs.size());
    List<BaseAdvancedRecord> subjectRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (isFiltered(record, config.getMappingFilters(), identifier)) {
        continue;
      }

      BuilderRecord mappedRecord = mapping(record, config.getMappingConfigs());
      BaseAdvancedRecord advancedRecord = toSPGRecord(mappedRecord, spgType);
      if (advancedRecord != null) {
        List<BaseAdvancedRecord> objectFusedRecords = subGraphFusing.subGraphFusing(advancedRecord);
        objectFusedRecords.forEach(r -> recordLinking.linking(r));
        advancedRecords.addAll(objectFusedRecords);

        recordLinking.linking(advancedRecord);
        recordPredicating.predicting(advancedRecord);
        subjectRecords.add(advancedRecord);
      }
    }
    List<BaseAdvancedRecord> subjectFusedRecord = subjectFusing.fusing(subjectRecords);
    advancedRecords.addAll(subjectFusedRecord);
    return (List) advancedRecords;
  }

  @Override
  public void close() throws Exception {}
}
