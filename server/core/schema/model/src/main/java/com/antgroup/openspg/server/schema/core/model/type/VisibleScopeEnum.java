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

package com.antgroup.openspg.server.schema.core.model.type;

/** Enumeration of visible domains of the SPG type. */
public enum VisibleScopeEnum {

  /** It is visible to all project */
  PUBLIC,

  /** It is visible to current domain */
  DOMAIN,

  /** It is visible to current project */
  PRIVATE,

  /** Can't be used for object. */
  INVISIBLE;

  public static VisibleScopeEnum toEnum(String value) {
    for (VisibleScopeEnum visibleScopeEnum : VisibleScopeEnum.values()) {
      if (visibleScopeEnum.name().equalsIgnoreCase(value)) {
        return visibleScopeEnum;
      }
    }

    return VisibleScopeEnum.PUBLIC;
  }
}
