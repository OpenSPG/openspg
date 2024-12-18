/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.interfaces.graphstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents a filter for graph query based on property values. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyFilter {

  /** The name of the property to filter on. */
  private String name;

  /**
   * The comparison operator to be used for filtering. E.g., EQ(equal to), GE(greater than or equal
   * to) etc.
   */
  private ComparisonOperator operator;

  /** The operand value to be compared against the property value using the specified operator. */
  private Object operand;
}
