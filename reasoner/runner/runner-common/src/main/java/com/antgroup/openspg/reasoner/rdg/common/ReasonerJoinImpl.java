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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.common.pattern.PartialGraphPattern;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.lube.logical.planning.FullOuterJoin$;
import com.antgroup.openspg.reasoner.lube.logical.planning.JoinType;
import com.antgroup.openspg.reasoner.lube.logical.planning.RightOuterJoin$;
import com.antgroup.openspg.reasoner.rdg.common.model.JoinItem;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;

public class ReasonerJoinImpl implements Serializable {
  private final String rootVertexAlias;
  private final JoinType joinType;
  private final KgGraphLeftJoinImpl leftJoinImpl;
  private final KgGraphRightJoinImpl rightJoinImpl;
  private final scala.collection.immutable.List<Var> varList;

  public ReasonerJoinImpl(
      JoinType joinType,
      List<String> onAlias,
      PartialGraphPattern leftSchema,
      PartialGraphPattern rightSchema,
      Long pathLimit) {
    this.rootVertexAlias = onAlias.get(0);
    this.joinType = joinType;
    this.leftJoinImpl =
        new KgGraphLeftJoinImpl(joinType, onAlias, leftSchema, rightSchema, pathLimit);
    this.rightJoinImpl = new KgGraphRightJoinImpl(onAlias, leftSchema);
    this.varList =
        Convert2ScalaUtil.toScalaList(
            onAlias.stream()
                .map((Function<String, Var>) s -> new NodeVar(s, null))
                .collect(Collectors.toList()));
  }

  public void join(Iterator joinItemIt, Consumer<KgGraph<IVertexId>> consumer) {
    Map<GroupByKeyItem, Tuple2<List<KgGraph<IVertexId>>, List<KgGraph<IVertexId>>>> joinMap =
        new HashMap<>();
    while (joinItemIt.hasNext()) {
      JoinItem joinItem = (JoinItem) joinItemIt.next();
      joinItemIt.remove();
      Object[] keys = RunnerUtil.getVarFromKgGraph(joinItem.getKgGraph(), varList);
      Tuple2<List<KgGraph<IVertexId>>, List<KgGraph<IVertexId>>> leftRightTuple2 =
          joinMap.computeIfAbsent(
              new GroupByKeyItem(keys), k -> new Tuple2<>(new ArrayList<>(), new ArrayList<>()));
      if (joinItem.isLeft()) {
        leftRightTuple2._1().add(joinItem.getKgGraph());
      } else {
        leftRightTuple2._2().add(joinItem.getKgGraph());
      }
    }

    Iterator<Map.Entry<GroupByKeyItem, Tuple2<List<KgGraph<IVertexId>>, List<KgGraph<IVertexId>>>>>
        joinMapIt = joinMap.entrySet().iterator();
    while (joinMapIt.hasNext()) {
      Map.Entry<GroupByKeyItem, Tuple2<List<KgGraph<IVertexId>>, List<KgGraph<IVertexId>>>>
          joinEntry = joinMapIt.next();
      joinMapIt.remove();

      List<KgGraph<IVertexId>> left = joinEntry.getValue()._1();
      List<KgGraph<IVertexId>> right = joinEntry.getValue()._2();
      this.leftJoinImpl.join(left, right).forEach(consumer);

      if (FullOuterJoin$.MODULE$.equals(this.joinType)
          || RightOuterJoin$.MODULE$.equals(this.joinType)) {
        if (CollectionUtils.isNotEmpty(left)) {
          continue;
        }
        this.rightJoinImpl.join(right).forEach(consumer);
      }
    }
  }

  public List<KgGraph<IVertexId>> join(Iterator joinItemIt) {
    ArrayList<KgGraph<IVertexId>> result = new ArrayList<>();
    join(joinItemIt, result::add);
    result.trimToSize();
    return result;
  }

  public List<KgGraph<IVertexId>> join(Collection<JoinItem> allJoinItems) {
    return join(allJoinItems.iterator());
  }
}
