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
package com.antgroup.openspg.server.biz.schema.model;

import com.antgroup.openspg.core.schema.model.OntologyId;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import java.util.List;
import lombok.Data;

@Data
public class NodeTypeModel {

  private OntologyId ontologyId;

  private String name;

  private String nameZh;

  private String desc;
  /** @see SPGTypeEnum */
  private String type;

  private String parentName;

  private String hypernymPredicate;

  private List<PropertyModel> properties;

  private List<EdgeTypeModel> relations;
}
