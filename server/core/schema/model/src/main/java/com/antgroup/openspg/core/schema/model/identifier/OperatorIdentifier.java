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

package com.antgroup.openspg.core.schema.model.identifier;

import java.util.Objects;

/** The identity of operator, consist by the name of the operator. */
public class OperatorIdentifier extends BaseSPGIdentifier {

  private static final long serialVersionUID = 7417123139682600501L;

  /** Unique name of operator */
  private final String name;

  public OperatorIdentifier(String name) {
    super(SPGIdentifierTypeEnum.OPERATOR);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OperatorIdentifier)) {
      return false;
    }
    OperatorIdentifier that = (OperatorIdentifier) o;
    return Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }
}
