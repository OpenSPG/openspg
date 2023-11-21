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

package com.antgroup.openspg.core.spgschema.model.identifier;

import java.util.Objects;

/** The unique name of SPO triple, it combined by the ame of subject, predicate and object. */
public class SPGTripleIdentifier extends BaseSPGIdentifier {

  private static final long serialVersionUID = -3593905994059059574L;

  /** The unique name of the subject schema type. */
  private BaseSPGIdentifier subject;

  /** The name of the predicate. */
  private PredicateIdentifier predicate;

  /** The unique name of the object schema type. */
  private BaseSPGIdentifier object;

  public SPGTripleIdentifier() {
    super(SPGIdentifierTypeEnum.SPG_TRIPLE);
  }

  public SPGTripleIdentifier(
      BaseSPGIdentifier subject, PredicateIdentifier predicate, BaseSPGIdentifier object) {
    super(SPGIdentifierTypeEnum.SPG_TRIPLE);
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  public BaseSPGIdentifier getSubject() {
    return subject;
  }

  public PredicateIdentifier getPredicate() {
    return predicate;
  }

  public BaseSPGIdentifier getObject() {
    return object;
  }

  @Override
  public String toString() {
    return subject + "$" + predicate + "$" + object;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SPGTripleIdentifier)) {
      return false;
    }
    SPGTripleIdentifier that = (SPGTripleIdentifier) o;
    return Objects.equals(getSubject(), that.getSubject())
        && Objects.equals(getPredicate(), that.getPredicate())
        && Objects.equals(getObject(), that.getObject());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSubject(), getPredicate(), getObject());
  }
}
