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

package com.antgroup.openspg.server.infra.dao.repository.spgschema.enums;

import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;

public enum ProjectEntityTypeEnum {
  ENTITY_TYPE,

  RELATION_TYPE;

  public static String getType(SPGOntologyEnum ontologyEnum) {
    switch (ontologyEnum) {
      case TYPE:
        return ENTITY_TYPE.name();
      case RELATION:
        return RELATION_TYPE.name();
      default:
        throw new IllegalArgumentException("illegal type=" + ontologyEnum);
    }
  }

  public static SPGOntologyEnum getOntologyType(String type) {
    ProjectEntityTypeEnum projectEntityTypeEnum = ProjectEntityTypeEnum.valueOf(type);
    switch (projectEntityTypeEnum) {
      case ENTITY_TYPE:
        return SPGOntologyEnum.TYPE;
      case RELATION_TYPE:
        return SPGOntologyEnum.RELATION;
      default:
        throw new IllegalArgumentException("illegal type=" + type);
    }
  }
}
