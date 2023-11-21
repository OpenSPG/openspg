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

package com.antgroup.openspg.core.spgschema.model.type;

/** Enumeration of reference sources */
public enum RefSourceEnum {

  /** Corekg */
  COREKG,

  /** Other project. */
  PROJECT;

  public static RefSourceEnum toEnum(String val) {
    for (RefSourceEnum refSourceEnum : RefSourceEnum.values()) {
      if (refSourceEnum.name().equalsIgnoreCase(val)) {
        return refSourceEnum;
      }
    }

    throw new IllegalArgumentException("unknown type: " + val);
  }
}
