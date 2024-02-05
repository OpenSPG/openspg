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

import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.pipeline.config.RelationMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.EdgeRecordConvertor;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.BaseOntology;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGIdentifierTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;

public class RelationMappingProcessor extends BaseProcessor<RelationMappingNodeConfig> {

  private Relation relation;
  private final RelationIdentifier identifier;
  private RecordLinking recordLinking;

  public RelationMappingProcessor(String id, String name, RelationMappingNodeConfig config) {
    super(id, name, config);
    this.identifier = RelationIdentifier.parse(config.getRelation());
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);

    this.relation = (Relation) loadSchema(identifier, context.getCatalog());

    this.recordLinking = new RecordLinkingImpl(config.getMappingConfigs());
    this.recordLinking.init(context);
  }

  protected BaseOntology loadSchema(BaseSPGIdentifier identifier, BuilderCatalog catalog) {
    SPGIdentifierTypeEnum identifierType = identifier.getIdentifierType();
    if (Objects.requireNonNull(identifierType) == SPGIdentifierTypeEnum.RELATION) {
      return catalog.getRelation((RelationIdentifier) identifier);
    }
    throw new IllegalArgumentException("illegal identifier type=" + identifierType);
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseRecord> spgRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      if (isFiltered(record, config.getMappingFilters(), identifier)) {
        continue;
      }

      BuilderRecord mappedRecord = mapping(record, config.getMappingConfigs());
      RelationRecord relationRecord = toSPGRecord(mappedRecord, relation);
      recordLinking.linking(relationRecord);
      spgRecords.add(relationRecord);
    }
    return spgRecords;
  }

  private boolean isFiltered(
      BuilderRecord record,
      List<SPGTypeMappingNodeConfig.MappingFilter> mappingFilters,
      BaseSPGIdentifier identifier) {
    if (record.getIdentifier() != null && !record.getIdentifier().equals(identifier)) {
      return true;
    }
    if (CollectionUtils.isEmpty(mappingFilters)) {
      return false;
    }
    for (SPGTypeMappingNodeConfig.MappingFilter mappingFilter : mappingFilters) {
      String columnName = mappingFilter.getColumnName();
      String columnValue = mappingFilter.getColumnValue();

      String propertyValue = record.getPropValue(columnName);
      if (columnValue.equals(propertyValue)) {
        return false;
      }
    }
    return true;
  }

  private BuilderRecord mapping(
      BuilderRecord record, List<SPGTypeMappingNodeConfig.MappingConfig> mappingConfigs) {
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      // if empty, perform mapping with the same name
      return record;
    }
    Map<String, String> newProps = new HashMap<>(record.getProps().size());
    for (SPGTypeMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      String source = mappingConfig.getSource();
      String target = mappingConfig.getTarget();

      String sourceValue = record.getPropValue(source);
      if (sourceValue != null) {
        newProps.put(target, sourceValue);
      }
    }
    return record.withNewProps(newProps);
  }

  private RelationRecord toSPGRecord(BuilderRecord record, Relation relation) {
    String srcId = record.getPropValue("srcId");
    String dstId = record.getPropValue("dstId");
    if (StringUtils.isBlank(srcId) || StringUtils.isBlank(dstId)) {
      throw new BuilderRecordException("");
    }
    return EdgeRecordConvertor.toRelationRecord(relation, srcId, dstId, record.getProps());
  }

  @Override
  public void close() throws Exception {}
}
