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

package com.antgroup.openspg.reasoner.udf.builtin.udtf;

import com.antgroup.openspg.reasoner.common.IConceptTree;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;

@UdfDefine(name = Constants.CONCEPT_EDGE_EXPAND_FUNC_NAME)
public class ConceptEdgeExpand extends BaseUdtf {

  private IConceptTree conceptTree;

  @Override
  public List<KgType> getInputRowTypes() {
    return null;
  }

  @Override
  public List<KgType> getResultTypes() {
    return null;
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
    String conceptId = (String) args.get(2);
    String conceptType = (String) args.get(3);

    Integer[] subLevels = new Integer[] {0};
    if (args.size() >= 5) {
      subLevels = (Integer[]) args.get(4);
    }

    Long internalId = (Long) source.get(Constants.VERTEX_INTERNAL_ID_KEY);
    String sourceVertexType = (String) source.get(Constants.CONTEXT_LABEL);
    SPO spo = new SPO(sourceVertexType, edgeType, conceptType);
    List<String> belongToConceptList =
        this.conceptTree.getBelongToConcept(
            IVertexId.from(internalId, sourceVertexType), spo.toString(), Direction.OUT);
    if (CollectionUtils.isEmpty(belongToConceptList)) {
      return;
    }

    boolean find = false;
    Map<Integer, List<String>> upperConceptMap = new HashMap<>();
    if (belongToConceptList.contains(conceptId)) {
      find = true;
      upperConceptMap.put(0, Lists.newArrayList(conceptId));
    } else {
      Queue<Tuple2<Integer, String>> checkConceptQueue = new LinkedList<>();
      for (String concept : belongToConceptList) {
        checkConceptQueue.add(new Tuple2<>(0, concept));
        List<String> conceptList = upperConceptMap.computeIfAbsent(0, k -> new ArrayList<>());
        conceptList.add(concept);
      }
      Tuple2<Integer, String> checkingConcept;
      while (null != (checkingConcept = checkConceptQueue.poll())) {
        String upper = this.conceptTree.getUpper(conceptType, checkingConcept._2());
        List<String> conceptList =
            upperConceptMap.computeIfAbsent(checkingConcept._1() + 1, k -> new ArrayList<>());
        conceptList.add(upper);
        if (StringUtils.isEmpty(upper)) {
          break;
        }
        if (upper.equals(conceptId)) {
          find = true;
          break;
        }
        checkConceptQueue.add(new Tuple2<>(checkingConcept._1() + 1, upper));
      }
    }
    if (!find) {
      return;
    }

    int maxLevel = Collections.max(upperConceptMap.keySet());
    Map<Integer, List<String>> revertLevelUpperConceptMap = new HashMap<>();
    for (Map.Entry<Integer, List<String>> entry : upperConceptMap.entrySet()) {
      revertLevelUpperConceptMap.put(Math.abs(maxLevel - entry.getKey()), entry.getValue());
    }

    List<Integer> subLevelList = Arrays.asList(subLevels);
    List<String> result = new ArrayList<>();
    for (int i = 0; i <= maxLevel; ++i) {
      boolean add = subLevelList.contains(i);
      if (add && revertLevelUpperConceptMap.containsKey(i)) {
        result.addAll(revertLevelUpperConceptMap.get(i));
      }
    }

    LinkedUdtfResult udtfResult = new LinkedUdtfResult();
    udtfResult.setEdgeType(edgeType);
    udtfResult.getTargetVertexIdList().addAll(result);
    forward(Lists.newArrayList(udtfResult));
  }
}
