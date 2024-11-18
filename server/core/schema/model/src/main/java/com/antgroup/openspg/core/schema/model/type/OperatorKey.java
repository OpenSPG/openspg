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

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.Objects;
import lombok.Getter;

/** Value object of operator key, include a name and a version. */
@Getter
public class OperatorKey extends BaseValObj {

  private static final long serialVersionUID = -7062235408406879995L;

  /** Operator name. */
  private final String name;

  /** Operator version. */
  private final Integer version;

  public OperatorKey(String name, Integer version) {
    this.name = name;
    this.version = version;
  }

  @Override
  public String toString() {
    return String.format("%s_%s", name, version);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OperatorKey that = (OperatorKey) o;
    return Objects.equals(name, that.name) && Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version);
  }
}
