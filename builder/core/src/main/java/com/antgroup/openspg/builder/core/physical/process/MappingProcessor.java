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

import com.antgroup.openspg.builder.core.physical.process.pattern.BaseMappingPattern;
import com.antgroup.openspg.builder.core.physical.process.pattern.MappingPatternFactory;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

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

  private final BaseMappingPattern mappingPattern;

  public MappingProcessor(String id, String name, MappingNodeConfig config) {
    super(id, name, config);
    mappingPattern = MappingPatternFactory.patternParse(config.getElements());
    mappingPattern.loadMappingConfig(config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    mappingPattern.loadAndCheckSchema(context.getProjectSchema());
    mappingPattern.loadPropertyMounter();
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> records) {
    return mappingPattern.mapping(records);
  }

  @Override
  public void close() {}
}
