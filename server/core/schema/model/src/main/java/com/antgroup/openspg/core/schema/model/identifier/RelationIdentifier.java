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

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class RelationIdentifier extends BaseSPGIdentifier {

  private final SPGTypeIdentifier start;

  private final PredicateIdentifier predicate;

  private final SPGTypeIdentifier end;

  public RelationIdentifier(
      SPGTypeIdentifier start, PredicateIdentifier predicate, SPGTypeIdentifier end) {
    super(SPGIdentifierTypeEnum.RELATION);
    this.start = start;
    this.predicate = predicate;
    this.end = end;
  }

  public static RelationIdentifier parse(String identifier) {
    String[] splits = identifier.split("_");
    if (splits.length != 3) {
      throw new IllegalArgumentException("illegal relation identifier=" + identifier);
    }
    return new RelationIdentifier(
        SPGTypeIdentifier.parse(splits[0]),
        new PredicateIdentifier(splits[1]),
        SPGTypeIdentifier.parse(splits[2]));
  }

  @Override
  public String toString() {
    return String.format("%s_%s_%s", start, predicate, end);
  }
}
