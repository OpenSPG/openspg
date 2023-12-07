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

package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.NodeTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MappingNodeConfig extends BaseNodeConfig {
  /**
   * 映射的元素，可能是一个或者多个实体类型，也可能是一个或者多个关系，也可能是一个子图 支持的几类语法： 1.
   * RiskMining.App/RiskMining.App_hasCert_RiskMining.Cert 支持单个类型或者单条边的映射 2.
   * (RiskMining.App|...)-[hasCert|...]->(RiskMining.Cert|...).+
   */
  private final String elements;

  /** 映射过滤器，在映射前将某些元素根据filter条件过滤 */
  private final List<MappingFilter> mappingFilters;

  /** The schema information for attributes, primarily consisting of operator information. */
  private final List<MappingSchema> mappingSchemas;

  /**
   * The configuration information for mapping nodes, which maps the upstream node properties to
   * schema attributes.
   */
  private final List<MappingConfig> mappingConfigs;

  private final transient SubgraphPattern elementsPattern;
  private final transient Map<String, List<MappingFilter>> mappingFiltersById = null;
  private final transient Map<String, List<MappingSchema>> mappingSchemasById = null;
  private final transient Map<String, List<MappingConfig>> mappingConfigsById = null;

  public MappingNodeConfig(
      String elements,
      List<MappingFilter> mappingFilters,
      List<MappingSchema> mappingSchemas,
      List<MappingConfig> mappingConfigs) {
    super(NodeTypeEnum.MAPPING);
    this.elements = elements;
    this.elementsPattern = SubgraphPattern.from(elements);
    this.mappingFilters = mappingFilters;
    this.mappingSchemas = mappingSchemas;
    this.mappingConfigs = mappingConfigs;
  }

  @Getter
  @AllArgsConstructor
  public static class MappingFilter extends BaseValObj {
    /** spg类型或者关系 */
    private final String identifier;

    /** The field name used for data filtering on upstream nodes. */
    private final String columnName;

    /** The field value used for data filtering on upstream nodes. */
    private final String columnValue;
  }

  @Getter
  @AllArgsConstructor
  public static class MappingSchema extends BaseValObj {
    /** spg类型或者关系 */
    private final String identifier;

    /** Schema field name with operator information. */
    private final String propertyName;

    /** 属性挂载策略配置，如果有多个策略则按照顺序返回第一个挂载成功的 */
    private final List<PropertyMounterConfig> propertyMounterConfigs;
  }

  @Getter
  @AllArgsConstructor
  public static class MappingConfig extends BaseValObj {
    /** spg类型或者关系 */
    private final String identifier;

    /** Raw field name. */
    private final String source;

    /** The list of schema fields corresponding to the raw field. */
    private final List<String> target;
  }
}
