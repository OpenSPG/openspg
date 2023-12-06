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

import com.antgroup.openspg.builder.core.BuilderException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.core.semantic.PropertyMounterFactory;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * Knowledge Mapping Component The main function is to map fields to schema, perform semantic
 * operations such as standardization and linkage operators, and output the results in spgRecord
 * mode.
 *
 * <p>The entire process consists of three steps.
 *
 * <ul>
 *   <li>Step 1: - Mapping the data source fields to the schema fields.
 *   <li>Step 2: - Executing operators on semantic fields, operators are bound to target entities or
 *       concepts, and can standardize attribute values or navigate through attribute value chains
 *       to target entities or concepts.
 *   <li>Step 3: - Derive relational predicates, such as "LeadTo" and "BelongTo" based on known
 *       knowledge and rules defined by the schema.
 * </ul>
 */
@Slf4j
public class MappingProcessor extends BaseProcessor<MappingNodeConfig> {

  private final Map<String, Map<String, List<PropertyMounter>>> propertyMounters = new HashMap<>();

  public MappingProcessor(String id, String name, MappingNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(RuntimeContext context) throws BuilderException {
    loadPropertyMounter();
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> records) {
    List<BaseRecord> resultRecords = new ArrayList<>();

    // 按照子图元素顺序进行处理，避免有些情况下o还没写入，先写入p
    // 然而有些图存储，比如tugraph不支持悬空边导致p无法写入
    for (String element : config.getElementsPattern().elementOrdered()) {
      List<BaseRecord> leftRecords = new ArrayList<>(records.size());
      for (BaseRecord baseRecord : records) {
        BuilderRecord record = (BuilderRecord) baseRecord;
        if (!element.equals(record.getIdentifier())) {
          leftRecords.add(record);
          continue;
        }

        // 判断当前record是否被过滤
        if (isFiltered(record)) {
          continue;
        }

        // 字段映射并转化为BaseSPGRecord
        BuilderRecord mappingRecord = mapping(record);
        BaseSPGRecord baseSPGRecord = toSpgRecord(mappingRecord);
        if (baseSPGRecord == null) {
          continue;
        }

        // 根据配置的策略执行属性挂载
        BaseSPGRecord propertyMountedRecord = propertyMount(baseSPGRecord);
        resultRecords.add(propertyMountedRecord);
      }
      records = leftRecords;
    }
    return resultRecords;
  }

  @Override
  public void close() {}

  private void loadPropertyMounter() {
    if (MapUtils.isEmpty(config.getMappingSchemasById())) {
      return;
    }

    for (Map.Entry<String, List<MappingNodeConfig.MappingSchema>> entry :
        config.getMappingSchemasById().entrySet()) {
      String identifier = entry.getKey();
      List<MappingNodeConfig.MappingSchema> mappingSchemas = entry.getValue();

      Map<String, List<PropertyMounter>> mounters = propertyMounters.get(identifier);
      if (mounters == null) {
        mounters = new HashMap<>(mappingSchemas.size());
      }

      for (MappingNodeConfig.MappingSchema mappingSchema : mappingSchemas) {
        List<PropertyMounter> propertyMounters =
            mappingSchema.getPropertyMounterConfigs().stream()
                .map(PropertyMounterFactory::getPropertyMounter)
                .collect(Collectors.toList());
        mounters.put(mappingSchema.getPropertyName(), propertyMounters);
      }
      propertyMounters.put(identifier, mounters);
    }
  }

  private boolean isFiltered(BuilderRecord record) {
    if (MapUtils.isEmpty(config.getMappingFiltersById())) {
      return false;
    }

    List<MappingNodeConfig.MappingFilter> mappingFilters =
        config.getMappingFiltersById().get(record.getIdentifier());
    if (CollectionUtils.isEmpty(mappingFilters)) {
      return false;
    }

    for (MappingNodeConfig.MappingFilter mappingFilter : mappingFilters) {
      String columnName = mappingFilter.getColumnName();
      String columnValue = mappingFilter.getColumnValue();

      String propertyValue = record.getPropValue(columnName);
      if (columnValue.equals(propertyValue)) {
        return false;
      }
    }
    return true;
  }

  private BuilderRecord mapping(BuilderRecord record) {
    Map<String, String> newProps = new HashMap<>(record.getProps().size());

    List<MappingNodeConfig.MappingConfig> mappingConfigs =
        config.getMappingConfigsById().get(record.getIdentifier());

    for (MappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      String source = mappingConfig.getSource();
      List<String> targets = mappingConfig.getTarget();

      String sourceValue = record.getPropValue(source);
      for (String target : targets) {
        newProps.put(target, sourceValue);
      }
    }
    return record.withNewProps(newProps);
  }

  private BaseSPGRecord toSpgRecord(BuilderRecord record) {
    return null;
    //    switch (config.getMappingType()) {
    //      case SPG_TYPE:
    //        String bizId = mappingResult.get("id");
    //        if (StringUtils.isBlank(bizId)) {
    //          return null;
    //        }
    //        return VertexRecordConvertor.toAdvancedRecord(spgType, bizId, mappingResult);
    //      case RELATION:
    //        String srcId = mappingResult.get("srcId");
    //        String dstId = mappingResult.get("dstId");
    //        if (StringUtils.isBlank(srcId) || StringUtils.isBlank(dstId)) {
    //          return null;
    //        }
    //        return EdgeRecordConvertor.toRelationRecord(relation, srcId, dstId, mappingResult);
    //      default:
    //        throw BuilderException.illegalMappingType(config.getMappingType().toString());
    //    }
  }

  private BaseSPGRecord propertyMount(BaseSPGRecord record) {
    if (!(record instanceof BaseAdvancedRecord)) {
      return record;
    }

    BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) record;
    String identifier = advancedRecord.getName();

    Map<String, List<PropertyMounter>> propertyMounters = this.propertyMounters.get(identifier);
    if (MapUtils.isEmpty(propertyMounters)) {
      return record;
    }

    for (SPGPropertyRecord propertyRecord : advancedRecord.getSpgProperties()) {
      if (!propertyRecord.getProperty().getObjectTypeRef().isAdvancedType()) {
        continue;
      }
      for (PropertyMounter propertyMounter : propertyMounters.get(propertyRecord.getName())) {
        if (propertyMounter.propertyMount(propertyRecord)) {
          break;
        }
      }
    }
    return record;
  }
}
