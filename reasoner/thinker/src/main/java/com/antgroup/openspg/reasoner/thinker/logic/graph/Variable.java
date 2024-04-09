/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.thinker.logic.graph;

import java.util.Objects;
import lombok.Data;

/**
 * @author kejian
 * @version Variable.java, v 0.1 2024年04月08日 6:00 PM kejian
 */
@Data
public class Variable implements Element {
  private String name;

  public Variable(String name) {
    this.name = name;
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
   * Setter method for property <tt>name</tt>.
   *
   * @param name value to be assigned to property name
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Variable)) {
      return false;
    }
    Variable variable = (Variable) o;
    return Objects.equals(name, variable.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
