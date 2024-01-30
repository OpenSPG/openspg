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

package com.antgroup.openspg.reasoner.udf.builtin.udtf;

import com.antgroup.openspg.reasoner.common.IConceptTree;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;

@UdfDefine(name = Constants.CONCEPT_EDGE_EXPAND_FUNC_NAME)
public class ConceptEdgeExpand extends BaseUdtf {

  private IConceptTree conceptTree;

  @Override
  public List<KgType> getInputRowTypes() {
    return Lists.newArrayList(
        KTObject$.MODULE$, KTString$.MODULE$, KTObject$.MODULE$, KTString$.MODULE$);
  }

  @Override
  public List<KgType> getResultTypes() {
    return Lists.newArrayList(KTObject$.MODULE$);
  }

  @Override
  public void initialize(Object... parameters) {
    if (parameters.length > 0) {
      conceptTree = (IConceptTree) parameters[0];
    }
  }

  @Override
  public void process(List<Object> args) {
    Map<String, Object> source = (Map<String, Object>) args.get(0);
    String edgeType = (String) args.get(1);
    String[] conceptIds = (String[]) args.get(2);
    String conceptType = (String) args.get(3);

    Long internalId = (Long) source.get(Constants.VERTEX_INTERNAL_ID_KEY);
    String sourceVertexType = (String) source.get(Constants.CONTEXT_LABEL);
    SPO spo = new SPO(sourceVertexType, edgeType, conceptType);
    List<String> belongToConceptList =
        this.conceptTree.getBelongToConcept(
            IVertexId.from(internalId, sourceVertexType), spo.toString(), Direction.OUT);
    if (CollectionUtils.isEmpty(belongToConceptList)) {
      return;
    }

    List<String> conceptIdList =
        Lists.newArrayList(conceptIds).stream()
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());
    if (conceptIdList.isEmpty()) {
      getAllConcept(belongToConceptList, conceptType, edgeType);
      return;
    }

    Set<String> checkingConceptIdSet = new HashSet<>(conceptIdList);

    Set<String> validConceptIdSet = new HashSet<>();

    Map<Integer, List<String>> upperConceptMap = new HashMap<>();
    Queue<Tuple2<Integer, String>> checkConceptQueue = new LinkedList<>();
    for (String concept : belongToConceptList) {
      if (checkingConceptIdSet.contains(concept)) {
        checkingConceptIdSet.remove(concept);
        validConceptIdSet.add(concept);
      }
      checkConceptQueue.add(new Tuple2<>(0, concept));
      List<String> conceptList = upperConceptMap.computeIfAbsent(0, k -> new ArrayList<>());
      conceptList.add(concept);
    }
    if (!checkingConceptIdSet.isEmpty()) {
      Tuple2<Integer, String> checkingConcept;
      while (null != (checkingConcept = checkConceptQueue.poll())) {
        String upper = this.conceptTree.getUpper(conceptType, checkingConcept._2());
        if (StringUtils.isEmpty(upper)) {
          break;
        }
        List<String> conceptList =
            upperConceptMap.computeIfAbsent(checkingConcept._1() + 1, k -> new ArrayList<>());
        conceptList.add(upper);
        if (checkingConceptIdSet.contains(upper)) {
          checkingConceptIdSet.remove(upper);
          validConceptIdSet.add(upper);
          if (checkingConceptIdSet.isEmpty()) {
            break;
          }
        }
        checkConceptQueue.add(new Tuple2<>(checkingConcept._1() + 1, upper));
      }
    }

    LinkedUdtfResult udtfResult = new LinkedUdtfResult();
    udtfResult.setEdgeType(edgeType);
    udtfResult.getTargetVertexIdList().addAll(validConceptIdSet);
    forward(Lists.newArrayList(udtfResult));
  }

  private void getAllConcept(
      List<String> belongToConceptList, String conceptType, String edgeType) {
    Set<String> validConceptIdSet = new HashSet<>();

    Queue<Tuple2<Integer, String>> checkConceptQueue = new LinkedList<>();
    for (String concept : belongToConceptList) {
      validConceptIdSet.add(concept);
      checkConceptQueue.add(new Tuple2<>(0, concept));
    }

    Tuple2<Integer, String> checkingConcept;
    while (null != (checkingConcept = checkConceptQueue.poll())) {
      String upper = this.conceptTree.getUpper(conceptType, checkingConcept._2());
      if (StringUtils.isEmpty(upper)) {
        break;
      }
      validConceptIdSet.add(upper);
      checkConceptQueue.add(new Tuple2<>(checkingConcept._1() + 1, upper));
    }
    LinkedUdtfResult udtfResult = new LinkedUdtfResult();
    udtfResult.setEdgeType(edgeType);
    udtfResult.getTargetVertexIdList().addAll(validConceptIdSet);
    forward(Lists.newArrayList(udtfResult));
  }
}
