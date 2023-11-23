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
import com.antgroup.openspg.builder.core.compiler.physical.invoker.concept.ConceptService;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.protocol.BaseAdvancedRecord;
import com.antgroup.openspg.builder.protocol.BaseSPGRecord;
import com.antgroup.openspg.builder.protocol.SPGPropertyRecord;
import com.antgroup.openspg.builder.protocol.SPGPropertyValue;
import com.antgroup.openspg.server.api.facade.client.SchemaFacade;
import com.antgroup.openspg.server.schema.core.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.server.schema.core.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.server.schema.core.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.server.schema.core.model.type.ConceptList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class LeadToPredicate implements ConceptPredicate<LogicalCausationSemantic> {

  private final SchemaFacade spgSchemaFacade = null;
  private final ConceptService conceptService;
  private final BelongToPredicate belongToPredicate;

  public LeadToPredicate(BelongToPredicate belongToPredicate, ConceptService conceptService) {
    this.belongToPredicate = belongToPredicate;
    this.conceptService = conceptService;
  }

  @Override
  public void init(RuntimeContext context) {}

  @Override
  public List<BaseSPGRecord> process(
      List<BaseSPGRecord> spgRecords, LogicalCausationSemantic leadTo) {
    List<BaseSPGRecord> results = new ArrayList<>(spgRecords);
    propagate(spgRecords, leadTo, results);
    return results;
  }

  private void propagate(
      List<BaseSPGRecord> spgRecords,
      LogicalCausationSemantic leadTo,
      List<BaseSPGRecord> results) {
    // 从spgRecords中筛选出本轮可传导的记录
    List<BaseAdvancedRecord> toPropagated = new ArrayList<>();
    for (BaseSPGRecord spgRecord : spgRecords) {
      if (!(spgRecord instanceof BaseAdvancedRecord)) {
        continue;
      }
      BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) spgRecord;
      SPGPropertyRecord belongToPropertyRecord =
          advancedRecord.getPredicateProperty(SystemPredicateEnum.BELONG_TO);
      if (belongToPropertyRecord == null) {
        // 如果没有归属于某个概念的，则也不进行传导，由于传导是基于概念的
        continue;
      }

      SPGPropertyValue propertyValue = belongToPropertyRecord.getValue();
      if (propertyValue == null || !propertyValue.contains(leadTo.getSubjectIdentifier())) {
        // 如果归属的概念不是当前leadTo的起始点，也不进行传导
        continue;
      }
      toPropagated.add(advancedRecord);
    }

    // 基于toPropagated开始本轮的事件传导
    for (BaseAdvancedRecord advancedRecord : toPropagated) {
      List<BaseSPGRecord> leadToRecords = new ArrayList<>();
      if (CollectionUtils.isEmpty(leadToRecords)) {
        continue;
      }
      results.addAll(leadToRecords);

      // 对leadTo出来的数据进行belongTo判断
      for (BaseSPGRecord leadToRecord : leadToRecords) {
        ConceptList conceptList = conceptService.query(leadToRecord);
        if (conceptList == null) {
          continue;
        }

        List<BaseSPGRecord> nextSpgRecords = Lists.newArrayList(leadToRecord);
        for (DynamicTaxonomySemantic belongTo :
            conceptList.getDynamicTaxonomyList(leadTo.getObjectIdentifier())) {
          nextSpgRecords = belongToPredicate.process(nextSpgRecords, belongTo);
        }

        for (LogicalCausationSemantic nextLeadTo :
            conceptList.getLogicalCausation(leadTo.getObjectIdentifier())) {
          propagate(nextSpgRecords, nextLeadTo, results);
        }
      }
    }
  }
}
