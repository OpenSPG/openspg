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
package com.antgroup.openspg.server.core.reasoner.service.udtf;

import com.antgroup.openspg.common.util.tuple.Tuple2;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexBizId;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.server.core.reasoner.service.impl.Utils;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author donghai.ydh
 * @version RdfProperty.java, v 0.1 2024-04-15 17:51 donghai.ydh
 */
@Slf4j
@UdfDefine(name = "rdf_expand")
public class RdfExpand extends BaseUdtf {
  private GraphState<IVertexId> graphState;
  private Map<String, Object> context;
  private String srcAlias;

  @Override
  public void initialize(Object... parameters) {
    if (parameters.length > 0) {
      graphState = (GraphState<IVertexId>) parameters[0];
    }
    if (parameters.length > 1) {
      context = (Map<String, Object>) parameters[1];
    }
    if (parameters.length > 2) {
      srcAlias = (String) parameters[2];
    }
  }

  @Override
  public List<KgType> getInputRowTypes() {
    return Lists.newArrayList(KTObject$.MODULE$, KTString$.MODULE$, KTObject$.MODULE$);
  }

  @Override
  public List<KgType> getResultTypes() {
    return Lists.newArrayList(KTObject$.MODULE$);
  }

  @Override
  public void process(List<Object> args) {
    // 获取起点id
    String vertexType = null;
    String bizId = null;
    Object s = context.get(srcAlias);
    if (s instanceof Map) {
      Map<String, Object> sMap = (Map<String, Object>) s;
      bizId = (String) sMap.get(Constants.NODE_ID_KEY);
      vertexType = (String) sMap.get(Constants.CONTEXT_LABEL);
    }
    IVertexId id = new VertexBizId(bizId, vertexType);

    // 结果
    Map<String, Set<Tuple2<String, String>>> validBizIdMap = Utils.getAllRdfEntity(graphState, id);

    for (Map.Entry<String, Set<Tuple2<String, String>>> entry : validBizIdMap.entrySet()) {
      LinkedUdtfResult udtfResult = new LinkedUdtfResult();
      udtfResult.setEdgeType(entry.getKey());
      for (Tuple2<String, String> data : entry.getValue()) {
        udtfResult.getTargetVertexIdList().add(data.first);
        udtfResult.getTargetVertexTypeList().add(data.second);
      }
      forward(Lists.newArrayList(udtfResult));
    }
  }
}
