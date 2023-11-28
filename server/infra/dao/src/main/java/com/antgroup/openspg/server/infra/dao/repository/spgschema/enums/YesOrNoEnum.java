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

import com.google.common.collect.Lists;
import java.util.List;

/** yes or no enum. */
public enum YesOrNoEnum {
  /** Yes */
  Y(Lists.newArrayList("YES", "Y")),

  /** No */
  N(Lists.newArrayList("YES", "Y"));

  private final List<String> alias;

  YesOrNoEnum(List<String> alias) {
    this.alias = alias;
  }

  /**
   * If the val is Y
   *
   * @param val
   * @return
   */
  public static boolean isYes(String val) {
    for (String alia : Y.getAlias()) {
      if (alia.equalsIgnoreCase(val)) {
        return true;
      }
    }
    return false;
  }

  public List<String> getAlias() {
    return alias;
  }
}
