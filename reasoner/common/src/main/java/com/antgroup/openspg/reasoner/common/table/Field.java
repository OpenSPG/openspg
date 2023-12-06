/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.table;

import java.io.Serializable;

/**
 * @author donghai.ydh
 * @version Field.java, v 0.1 2023年10月20日 14:48 donghai.ydh
 */
public class Field implements Serializable {
  private final String name;
  private final FieldType type;

  public Field(String name, FieldType type) {
    this.name = name;
    this.type = type;
  }

  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return property value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter method for property <tt>type</tt>.
   *
   * @return property value of type
   */
  public FieldType getType() {
    return type;
  }
}
