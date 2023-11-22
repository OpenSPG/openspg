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

package com.antgroup.openspg.server.schema.core.model.predicate;

import com.antgroup.openspg.server.common.model.base.BaseValObj;

/**
 * The configuration of the property mounted concept<br>
 *
 * <p>When a concept type is selected for the property, it can be mounted to a concept subtree or a
 * certain layer. the configuration will be used to find the matching concept for property when
 * importing property data. <br>
 * <br>
 * For example, user's native place property，the range type is AdminArea, mounted to city layer, so
 * the configuration is like this:
 *
 * <ul>
 *   <li>conceptBranch: China
 *   <li>conceptLayer: city
 * </ul>
 */
public class MountedConceptConfig extends BaseValObj {

  private static final long serialVersionUID = -8116179573933561438L;

  /**
   * If property is mounted to ConceptType, then the value = null, else if it mounted to a layer or
   * a concept ,then the value = concept value.
   */
  private String conceptBranch;

  /** The layer name if the property is mounted to a layer. */
  private String conceptLayer;

  public MountedConceptConfig() {}

  public MountedConceptConfig(String conceptBranch, String conceptLayer) {
    this.conceptBranch = conceptBranch;
    this.conceptLayer = conceptLayer;
  }

  public String getConceptBranch() {
    return conceptBranch;
  }

  public void setConceptBranch(String conceptBranch) {
    this.conceptBranch = conceptBranch;
  }

  public String getConceptLayer() {
    return conceptLayer;
  }

  public void setConceptLayer(String conceptLayer) {
    this.conceptLayer = conceptLayer;
  }
}
