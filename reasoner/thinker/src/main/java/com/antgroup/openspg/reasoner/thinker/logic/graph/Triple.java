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
package com.antgroup.openspg.reasoner.thinker.logic.graph;

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException;
import java.util.Objects;
import lombok.Data;

@Data
public class Triple extends Element {
  private Element subject;
  private Element predicate;
  private Element object;

  public Triple() {}

  public Triple(Element subject, Element predicate, Element object) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  @Override
  public boolean matches(Element other) {
    if (other instanceof Triple) {
      return subject.matches(((Triple) other).subject)
          && predicate.matches(((Triple) other).predicate)
          && object.matches(((Triple) other).object);
    } else {
      return false;
    }
  }

  @Override
  public Element bind(Element pattern) {
    if (pattern instanceof Triple) {
      return new Triple(
          subject.bind(((Triple) pattern).getSubject()),
          predicate.bind(((Triple) pattern).getPredicate()),
          object.bind(((Triple) pattern).getObject()));
    } else {
      throw new UnsupportedOperationException("Triple cannot bind " + pattern.toString(), null);
    }
  }

  public String alias() {
    return predicate.alias();
  }

  @Override
  public Element cleanAlias() {
    return new Triple(subject.cleanAlias(), predicate.cleanAlias(), object.cleanAlias());
  }

  public static Triple create(Element s, Element p, Element o) {
    return new Triple(nullToAny(s), nullToAny(p), nullToAny(o));
  }

  public static Triple create(Element element) {
    if (element instanceof Node || element instanceof Entity) {
      return new Triple(Element.ANY, Predicate.CONCLUDE, element);
    } else {
      return (Triple) element;
    }
  }

  private static Element nullToAny(Element n) {
    return n == null ? ANY : n;
  }

  /**
   * Getter method for property <tt>subject</tt>.
   *
   * @return property value of subject
   */
  public Element getSubject() {
    return subject;
  }

  /**
   * Setter method for property <tt>subject</tt>.
   *
   * @param subject value to be assigned to property subject
   */
  public void setSubject(Element subject) {
    this.subject = subject;
  }

  /**
   * Getter method for property <tt>predicate</tt>.
   *
   * @return property value of predicate
   */
  public Element getPredicate() {
    return predicate;
  }

  /**
   * Setter method for property <tt>predicate</tt>.
   *
   * @param predicate value to be assigned to property predicate
   */
  public void setPredicate(Element predicate) {
    this.predicate = predicate;
  }

  /**
   * Getter method for property <tt>object</tt>.
   *
   * @return property value of object
   */
  public Element getObject() {
    return object;
  }

  /**
   * Setter method for property <tt>object</tt>.
   *
   * @param object value to be assigned to property object
   */
  public void setObject(Element object) {
    this.object = object;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Triple)) {
      return false;
    }
    Triple triple = (Triple) o;
    return Objects.equals(subject, triple.subject)
        && Objects.equals(predicate, triple.predicate)
        && Objects.equals(object, triple.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, predicate, object);
  }

  @Override
  public String shortString() {
    StringBuilder sb = new StringBuilder();
    sb.append(subject.alias())
        .append("_")
        .append(predicate.alias())
        .append("_")
        .append(object.alias());
    return sb.toString();
  }
}
