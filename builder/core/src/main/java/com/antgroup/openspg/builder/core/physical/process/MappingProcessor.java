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
import com.antgroup.openspg.builder.core.physical.BuilderRecord;
import com.antgroup.openspg.builder.core.physical.invoker.InvokerFactory;
import com.antgroup.openspg.builder.core.physical.invoker.InvokerParam;
import com.antgroup.openspg.builder.core.physical.invoker.impl.InvokerFactoryImpl;
import com.antgroup.openspg.builder.core.runtime.BuilderRecordException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.SPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.VertexRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.BaseQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.MatchQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.OperatorType;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.QueryGroup;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.constraint.Constraint;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.antgroup.openspg.server.api.facade.client.SchemaFacade;
import com.antgroup.openspg.server.api.facade.dto.schema.request.RelationRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.server.api.http.client.HttpSchemaFacade;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import lombok.Setter;
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

  private final Map<String, OperatorConfig> property2Operator = new HashMap<>();
  @Setter private SchemaFacade schemaFacade;
  private BaseSPGType spgType;
  private Relation relation;
  private InvokerFactory invokerFactory;
  private SearchEngineClient searchEngineClient;

  public MappingProcessor(String id, String name, MappingNodeConfig config) {
    super(id, name, config);
    schemaFacade = new HttpSchemaFacade(true);
  }

  @Override
  public void doInit(RuntimeContext context) {
    searchEngineClient =
        SearchEngineClientDriverManager.getClient(context.getSearchEngineConnectionInfo());
    String schemaUrl = context.getSchemaUrl();
    if (StringUtils.isNotBlank(schemaUrl)) {
      HttpClientBootstrap.init(new ConnectionInfo(schemaUrl));
    }
    loadSchema();

    invokerFactory = new InvokerFactoryImpl();
    invokerFactory.init(context);
    loadOperator();
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> records) {
    List<BaseRecord> resultRecords = new ArrayList<>();
    for (BaseRecord baseRecord : records) {
      BuilderRecord record = (BuilderRecord) baseRecord;
      Map<String, String> properties = record.getProps();
      if (MapUtils.isEmpty(properties) || isFiltered(properties)) {
        continue;
      }

      Map<String, String> mappingResult = mapping(properties);
      BaseSPGRecord baseSpgRecord = toSpgRecord(mappingResult);
      if (baseSpgRecord == null) {
        continue;
      }

      List<BaseRecord> invokedRecords =
          invokerFactory.invoke(new InvokerParam(this, baseSpgRecord, property2Operator));

      if (context.isEnableSearchEngine()) {
        propertyMount(invokedRecords);
      }
      resultRecords.addAll(invokedRecords);
    }
    return resultRecords;
  }

  @Override
  public void close() {}

  private void loadSchema() {
    switch (config.getMappingType()) {
      case SPG_TYPE:
        spgType =
            schemaFacade
                .querySPGType(new SPGTypeRequest().setName(config.getSpgName()))
                .getDataThrowsIfNull(config.getSpgName());
        break;
      case RELATION:
        relation =
            schemaFacade
                .queryRelation(RelationRequest.parse(config.getSpgName()))
                .getDataThrowsIfNull(config.getSpgName());
        break;
      default:
        throw BuilderException.illegalMappingType(config.getMappingType().toString());
    }
  }

  private void loadOperator() {
    if (CollectionUtils.isEmpty(config.getMappingSchemas())) {
      return;
    }
    for (MappingNodeConfig.MappingSchema mappingSchema : config.getMappingSchemas()) {
      OperatorConfig operatorConfig = mappingSchema.getOperatorConfig();
      property2Operator.put(mappingSchema.getName(), mappingSchema.getOperatorConfig());
      invokerFactory.register(operatorConfig);
    }
  }

  private boolean isFiltered(Map<String, String> properties) {
    if (CollectionUtils.isEmpty(config.getMappingFilters())) {
      return false;
    }

    for (MappingNodeConfig.MappingFilter mappingFilter : config.getMappingFilters()) {
      String columnName = mappingFilter.getColumnName();
      String columnValue = mappingFilter.getColumnValue();

      String propertyValue = properties.get(columnName);
      if (columnValue.equals(propertyValue)) {
        return false;
      }
    }
    return true;
  }

  private Map<String, String> mapping(Map<String, String> properties) {
    Map<String, String> results = new HashMap<>();

    for (MappingNodeConfig.MappingConfig mappingConfig : config.getMappingConfigs()) {
      String source = mappingConfig.getSource();
      List<String> targets = mappingConfig.getTarget();

      String sourceValue = properties.get(source);
      for (String target : targets) {
        results.put(target, sourceValue);
      }
    }
    return results;
  }

  private BaseSPGRecord toSpgRecord(Map<String, String> mappingResult) {
    switch (config.getMappingType()) {
      case SPG_TYPE:
        String bizId = mappingResult.get("id");
        if (StringUtils.isBlank(bizId)) {
          return null;
        }
        return VertexRecordConvertor.toAdvancedRecord(spgType, bizId, mappingResult);
      case RELATION:
        String srcId = mappingResult.get("srcId");
        String dstId = mappingResult.get("dstId");
        if (StringUtils.isBlank(srcId) || StringUtils.isBlank(dstId)) {
          return null;
        }
        return EdgeRecordConvertor.toRelationRecord(relation, srcId, dstId, mappingResult);
      default:
        throw BuilderException.illegalMappingType(config.getMappingType().toString());
    }
  }

  private void propertyMount(List<BaseRecord> records) {
    for (BaseRecord record : records) {
      if (!(record instanceof BaseAdvancedRecord)) {
        continue;
      }
      BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) record;
      for (SPGPropertyRecord prop : advancedRecord.getSpgProperties()) {
        if (prop.getSpgTypeEnum() != SPGTypeEnum.CONCEPT_TYPE
            && prop.getSpgTypeEnum() != SPGTypeEnum.ENTITY_TYPE) {
          continue;
        }
        Set<String> mountedValues = new HashSet<>();
        String mountedValue = null;
        Constraint constraint = prop.getPropertyType().getConstraint();
        if (constraint != null && constraint.contains(ConstraintTypeEnum.MULTI_VALUE)) {
          for (String singleId : prop.getValue().getSplitIds()) {
            mountedValue =
                mount(prop.getSpgTypeEnum(), prop.getObjectTypeRef().getName(), singleId);
            mountedValues.add(mountedValue);
          }
        } else {
          mountedValue =
              mount(
                  prop.getSpgTypeEnum(),
                  prop.getObjectTypeRef().getName(),
                  prop.getValue().getIds());
          mountedValues.add(mountedValue);
        }
        String mountedValueStr = String.join(",", mountedValues);
        if (prop.getSpgTypeEnum() == SPGTypeEnum.CONCEPT_TYPE) {
          prop.getValue().setStd(mountedValueStr);
        }
        prop.getValue().setIds(mountedValueStr);
      }
    }
  }

  private String mount(SPGTypeEnum spgTypeEnum, String type, String value) {
    switch (spgTypeEnum) {
      case CONCEPT_TYPE:
        String concept = recall(type, value, SPGTypeEnum.CONCEPT_TYPE);
        if (StringUtils.isBlank(concept)) {
          throw new BuilderRecordException(
              this, "spgType={}, value={} concept mount failed", type, value);
        }
        return concept;
      case ENTITY_TYPE:
        String name = recall(type, value, SPGTypeEnum.ENTITY_TYPE);
        if (StringUtils.isBlank(name)) {
          throw new BuilderRecordException(
              this, "spgType={}, value={} entity mount failed", type, value);
        }
        return name;
      default:
        throw new IllegalArgumentException("illegal spgType=" + spgTypeEnum);
    }
  }

  private String recall(String type, String value, SPGTypeEnum spgTypeEnum) {
    SearchRequest request = new SearchRequest();
    request.setIndexName(searchEngineClient.getIdxNameConvertor().convertIdxName(type));
    MatchQuery idQuery = new MatchQuery("id", value);
    MatchQuery nameQuery = new MatchQuery("name", value);

    List<BaseQuery> queries = new ArrayList<>(4);
    queries.add(idQuery);
    queries.add(nameQuery);
    if (spgTypeEnum == SPGTypeEnum.CONCEPT_TYPE) {
      queries.add(new MatchQuery("alias", value));
      queries.add(new MatchQuery("stdId", value));
    }
    QueryGroup queryGroup = new QueryGroup(queries, OperatorType.OR);
    request.setQuery(queryGroup);
    request.setSize(50);
    List<IdxRecord> lst = searchEngineClient.search(request);
    if (CollectionUtils.isEmpty(lst)) {
      return null;
    }
    IdxRecord record = lst.get(0);
    if (!value.equals(record.getDocId()) && record.getScore() < 0.5) {
      return null;
    }
    return record.getDocId();
  }
}
