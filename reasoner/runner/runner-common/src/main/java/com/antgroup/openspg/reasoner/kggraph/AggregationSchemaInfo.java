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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph;

import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AggregationSchemaInfo implements Serializable {

    private final Map<String, List<Tuple2<String, Boolean>>> edgeEndpointMap  = new HashMap<>();
    private final Map<String, List<Tuple2<String, Boolean>>> vertexHasEdgeMap = new HashMap<>();

    /**
     * schema info
     */
    public AggregationSchemaInfo(Pattern schema) {
        for (Connection pc : RunnerUtil.getConnectionSet(schema)) {
            edgeEndpointMap.put(pc.alias(), Lists.newArrayList(
                    new Tuple2<>(pc.source(), true),
                    new Tuple2<>(pc.target(), false)
            ));
            List<Tuple2<String, Boolean>> sourceEdgeInfoList = vertexHasEdgeMap.computeIfAbsent(pc.source(), k -> new ArrayList<>());
            sourceEdgeInfoList.add(new Tuple2<>(pc.alias(), true));
            List<Tuple2<String, Boolean>> targetEdgeInfoList = vertexHasEdgeMap.computeIfAbsent(pc.target(), k -> new ArrayList<>());
            targetEdgeInfoList.add(new Tuple2<>(pc.alias(), false));
        }
    }

    /**
     * Getter method for property <tt>edgeEndpointMap</tt>.
     *
     * @return property value of edgeEndpointMap
     */
    public Map<String, List<Tuple2<String, Boolean>>> getEdgeEndpointMap() {
        return edgeEndpointMap;
    }

    /**
     * Getter method for property <tt>vertexHasEdgeMap</tt>.
     *
     * @return property value of vertexHasEdgeMap
     */
    public Map<String, List<Tuple2<String, Boolean>>> getVertexHasEdgeMap() {
        return vertexHasEdgeMap;
    }

}