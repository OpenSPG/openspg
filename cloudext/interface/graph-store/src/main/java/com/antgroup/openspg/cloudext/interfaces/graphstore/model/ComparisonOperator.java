/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.interfaces.graphstore.model;

import lombok.Getter;

@Getter
public enum ComparisonOperator {

  /** equal to. */
  EQ("="),

  /** not equal to. */
  NE("<>"),

  /** greater than or equal to. */
  GE(">="),

  /** greater than */
  GT(">"),

  /** less than or equal to */
  LE("<="),

  /** less than */
  LT("<"),

  /** in */
  IN("IN");

  /** sign of the comparison operator */
  private final String sign;

  ComparisonOperator(String sign) {
    this.sign = sign;
  }
}
