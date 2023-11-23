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

package com.antgroup.openspg.builder.core.pipeline.config;

import com.antgroup.openspg.builder.core.pipeline.enums.MappingTypeEnum;
import com.antgroup.openspg.builder.core.pipeline.enums.NodeTypeEnum;
import java.util.List;
import lombok.Getter;

@Getter
public class MappingNodeConfig extends BaseNodeConfig {

  /** The mapping target type of the node, including SPG entity or relation. */
  private final String spgName;

  /** The mapping types of the node. */
  private final MappingTypeEnum mappingType;

  /** The field configurations used for data filtering in mapping nodes. */
  private final List<MappingFilter> mappingFilters;

  /** The schema information for attributes, primarily consisting of operator information. */
  private final List<MappingSchema> mappingSchemas;

  /**
   * The configuration information for mapping nodes, which maps the upstream node properties to
   * schema attributes.
   */
  private final List<MappingConfig> mappingConfigs;

  public MappingNodeConfig(
      String spgName,
      MappingTypeEnum mappingType,
      List<MappingFilter> mappingFilters,
      List<MappingSchema> mappingSchemas,
      List<MappingConfig> mappingConfigs) {
    super(NodeTypeEnum.MAPPING);
    this.spgName = spgName;
    this.mappingType = mappingType;
    this.mappingFilters = mappingFilters;
    this.mappingSchemas = mappingSchemas;
    this.mappingConfigs = mappingConfigs;
  }

  @Getter
  public static class MappingFilter {

    /** The field name used for data filtering on upstream nodes. */
    private final String columnName;

    /** The field value used for data filtering on upstream nodes. */
    private final String columnValue;

    public MappingFilter(String columnName, String columnValue) {
      this.columnName = columnName;
      this.columnValue = columnValue;
    }
  }

  @Getter
  public static class MappingSchema {

    /** Schema field name with operator information. */
    private final String name;

    /** Operator configuration for property. */
    private final OperatorConfig operatorConfig;

    public MappingSchema(String name, OperatorConfig operatorConfig) {
      this.name = name;
      this.operatorConfig = operatorConfig;
    }
  }

  @Getter
  public static class MappingConfig {

    /** Raw field name. */
    private final String source;

    /** The list of schema fields corresponding to the raw field. */
    private final List<String> target;

    public MappingConfig(String source, List<String> target) {
      this.source = source;
      this.target = target;
    }
  }
}
