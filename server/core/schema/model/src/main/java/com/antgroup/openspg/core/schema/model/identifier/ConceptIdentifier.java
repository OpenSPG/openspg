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

package com.antgroup.openspg.core.schema.model.identifier;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/** The identifier of concept, consists by id and name. */
public class ConceptIdentifier extends BaseSPGIdentifier {

  private static final long serialVersionUID = -6128518833361508382L;

  /** The seperator symbol between parent and child concept names. */
  public static final String SEPARATOR = "-";

  /** Unique id of concept node. */
  private final String id;

  /** Unique name of concept node */
  private final String name;

  public ConceptIdentifier(String id) {
    super(SPGIdentifierTypeEnum.CONCEPT);
    this.id = id;
    this.name = genName(id);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  private String genName(String id) {
    String[] splits = id.split(SEPARATOR);
    return splits[splits.length - 1];
  }

  public String getFatherId() {
    String[] splits = id.split(SEPARATOR);
    if (splits.length <= 1) {
      return StringUtils.EMPTY;
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < splits.length - 2; i++) {
        stringBuilder.append(splits[i]).append(SEPARATOR);
      }
      stringBuilder.append(splits[splits.length - 2]);
      return stringBuilder.toString();
    }
  }

  @Override
  public String toString() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConceptIdentifier)) {
      return false;
    }
    ConceptIdentifier that = (ConceptIdentifier) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
