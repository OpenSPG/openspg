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
import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.VertexRecordConvertor;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

@Slf4j
public class SPGTypeMappingProcessor extends BaseMappingProcessor<SPGTypeMappingNodeConfig> {

  private BaseSPGType spgType;
  private Map<String, List<PropertyMounter>> propertyMounters;

  public SPGTypeMappingProcessor(String id, String name, SPGTypeMappingNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);

    SPGTypeIdentifier identifier = SPGTypeIdentifier.parse(config.getSpgType());
    this.spgType = (BaseSPGType) loadSchema(identifier, context.getProjectSchema());
    this.propertyMounters = loadPropertyMounters(config.getMappingConfigs());
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseRecord> spgRecords = new ArrayList<>(inputs.size());
    for (BaseRecord baseRecord : inputs) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      BaseAdvancedRecord advancedRecord =
          spgTypeRecordMapping(record, spgType, config, propertyMounters);
      if (advancedRecord != null) {
        spgRecords.add(advancedRecord);
      }
    }
    return spgRecords;
  }

  public static BaseAdvancedRecord spgTypeRecordMapping(
      BuilderRecord record,
      BaseSPGType spgType,
      SPGTypeMappingNodeConfig mappingConfig,
      Map<String, List<PropertyMounter>> propertyMounters) {
    if (isFiltered(record, mappingConfig.getMappingFilters())) {
      return null;
    }

    BuilderRecord mappedRecord = mapping(record, mappingConfig.getMappingConfigs());
    BaseAdvancedRecord advancedRecord = toSPGRecord(mappedRecord, spgType);
    propertyMount(advancedRecord, propertyMounters);
    return advancedRecord;
  }

  private static BaseAdvancedRecord toSPGRecord(BuilderRecord record, BaseSPGType spgType) {
    String bizId = record.getPropValue("id");
    if (StringUtils.isBlank(bizId)) {
      throw new BuilderRecordException("");
    }
    return VertexRecordConvertor.toAdvancedRecord(spgType, bizId, record.getProps());
  }

  private static void propertyMount(
      BaseAdvancedRecord advancedRecord, Map<String, List<PropertyMounter>> propertyMounters) {
    if (MapUtils.isEmpty(propertyMounters)) {
      return;
    }

    for (SPGPropertyRecord propertyRecord : advancedRecord.getSpgProperties()) {
      if (!propertyRecord.getProperty().getObjectTypeRef().isAdvancedType()) {
        continue;
      }
      List<PropertyMounter> mounters = propertyMounters.get(propertyRecord.getName());
      if (CollectionUtils.isEmpty(mounters)) {
        continue;
      }
      mounters.forEach(mounter -> mounter.propertyMount(propertyRecord));
    }
  }

  @Override
  public void close() throws Exception {}
}
