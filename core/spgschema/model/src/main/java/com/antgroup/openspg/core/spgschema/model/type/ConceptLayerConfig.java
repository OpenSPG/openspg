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

package com.antgroup.openspg.core.spgschema.model.type;

import com.antgroup.openspg.common.model.base.BaseValObj;
import java.util.List;

/**
 * The config of concept layer.
 *
 * <p>Each ConceptType is a kind of Classification of entity, it usually has a tree hierarchy, So we
 * can specify a name for each level of the concept tree to better express the hierarchical
 * structure of the concept。<br>
 * Taking administrative divisions as an example, the layer config can be: country, province, city,
 * district.
 */
public class ConceptLayerConfig extends BaseValObj {

  private static final long serialVersionUID = 6743796174241317223L;

  /** the predicate name */
  private String hypernymPredicate;

  /** The every layer's name of concept tree. */
  private List<String> layerNames;

  public ConceptLayerConfig() {}

  public ConceptLayerConfig(String hypernymPredicate, List<String> layerNames) {
    this.hypernymPredicate = hypernymPredicate;
    this.layerNames = layerNames;
  }

  public String getHypernymPredicate() {
    return hypernymPredicate;
  }

  public List<String> getLayerNames() {
    return layerNames;
  }

  public void setHypernymPredicate(String hypernymPredicate) {
    this.hypernymPredicate = hypernymPredicate;
  }

  public void setLayerNames(List<String> layerNames) {
    this.layerNames = layerNames;
  }
}
