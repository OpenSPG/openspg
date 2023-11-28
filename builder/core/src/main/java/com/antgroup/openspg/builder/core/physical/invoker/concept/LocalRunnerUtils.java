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

package com.antgroup.openspg.builder.core.physical.invoker.concept;

import com.antgroup.kg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.kg.reasoner.catalog.impl.OpenKgCatalog;
import com.antgroup.kg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.kg.reasoner.graphstate.GraphState;
import com.antgroup.kg.reasoner.graphstate.impl.CloudExtGraphState;
import com.antgroup.kg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.common.model.datasource.connection.GraphStoreConnectionInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class LocalRunnerUtils {

  public static Catalog buildCatalog(Long projectId, KgSchemaConnectionInfo connInfo) {
    //    Catalog catalog = new OpenKgCatalog(projectId, connInfo, null);
    //    catalog.init();
    //    return catalog;
    return null;
  }

  public static GraphState<IVertexId> buildGraphState(GraphStoreConnectionInfo connInfo) {
    CloudExtGraphState cloudExtGraphState = new CloudExtGraphState();

//    Map<String, Object> params = new HashMap<>();
//    params.put("cloudext.graphstore.schema", connInfo.getScheme());
//    params.putAll(connInfo.getParams());
//    cloudExtGraphState.init((Map) Collections.unmodifiableMap(params));
    return cloudExtGraphState;
  }
}
