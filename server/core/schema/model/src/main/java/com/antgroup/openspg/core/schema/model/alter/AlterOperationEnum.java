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

package com.antgroup.openspg.core.schema.model.alter;

/** Enumeration of schema alter operations. */
public enum AlterOperationEnum {

  /** Create operation, such as creating a new SPG type, property or relation. */
  CREATE,

  /** Update operation, such as updating the structure of SPG type, property or relation. */
  UPDATE,

  /** Delete operation, such as deleting a SPG type, property or relation. */
  DELETE,
  ;

  public static AlterOperationEnum toEnum(String name) {
    for (AlterOperationEnum alterOperationEnum : AlterOperationEnum.values()) {
      if (alterOperationEnum.name().equalsIgnoreCase(name)) {
        return alterOperationEnum;
      }
    }
    throw new IllegalArgumentException("unknown type: " + name);
  }
}
