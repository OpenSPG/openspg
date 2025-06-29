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

package com.antgroup.openspg.core.schema.model.type;

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import java.util.List;

/**
 * Class definition of the Index. <br>
 *
 * <p>this is a index type
 */
public class IndexType extends BaseAdvancedType {

  private static final long serialVersionUID = 7548382967010252528L;

  public IndexType(
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      List<Property> properties,
      List<Relation> relations,
      SPGTypeAdvancedConfig advancedConfig) {
    super(basicInfo, parentTypeInfo, SPGTypeEnum.INDEX_TYPE, properties, relations, advancedConfig);
  }
}
