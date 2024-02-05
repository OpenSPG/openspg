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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;

/** Edge schema of TuGraph. */
public class TuGraphEdgeType extends BaseTuGraphOntology {

  /** Constraints (for edge) */
  @JSONField(name = "constraints")
  private List<List<String>> constraints;

  /**
   * Getter method for property <tt>constraints</tt>.
   *
   * @return property value of constraints
   */
  public List<List<String>> getConstraints() {
    return constraints;
  }

  /**
   * Setter method for property <tt>constraints</tt>.
   *
   * @param constraints value to be assigned to property constraints
   */
  public void setConstraints(List<List<String>> constraints) {
    this.constraints = constraints;
  }
}
