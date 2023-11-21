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

package com.antgroup.openspg.core.spgschema.model.alter;

/** Enumeration of schema alter states */
public enum AlterStatusEnum {
  /** The spg ontology is published online */
  ONLINE,

  /** The spg ontology is deleted. */
  DELETED;

  public static AlterStatusEnum toEnum(String value) {
    for (AlterStatusEnum alterStatus : AlterStatusEnum.values()) {
      if (alterStatus.name().equalsIgnoreCase(value)) {
        return alterStatus;
      }
    }

    throw new IllegalArgumentException("unknown type: " + value);
  }
}
