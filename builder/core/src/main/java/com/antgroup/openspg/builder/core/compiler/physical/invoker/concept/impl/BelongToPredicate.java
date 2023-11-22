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

package com.antgroup.openspg.builder.core.compiler.physical.invoker.concept.impl;

import com.antgroup.openspg.builder.core.compiler.physical.invoker.concept.ConceptPredicate;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.protocol.BaseSPGRecord;
import com.antgroup.openspg.server.schema.core.model.semantic.DynamicTaxonomySemantic;
import java.util.List;

public class BelongToPredicate implements ConceptPredicate<DynamicTaxonomySemantic> {

  @Override
  public void init(RuntimeContext context) {}

  @Override
  public List<BaseSPGRecord> process(
      List<BaseSPGRecord> spgRecords, DynamicTaxonomySemantic belongTo) {
    return spgRecords;
  }
}
