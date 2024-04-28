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

  public static Triple create(Element s, Element p, Element o) {
    return new Triple(nullToAny(s), nullToAny(p), nullToAny(o));
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
}
