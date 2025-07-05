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

package com.antgroup.openspg.reasoner.runner.local.loader;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.google.common.collect.Lists;

import java.util.List;

public class TestFanxiqianGraphLoader2 extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
        return Lists.newArrayList(
                constructionVertex("s1", "SourceNumber", "id", "s1"),
                constructionVertex(
                        "r1", "Record", "id", "r1", "callDuration", "301"),
                constructionVertex(
                        "d1", "DestNumber", "id", "d1"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
        return Lists.newArrayList(
                constructionEdge("s1", "hasRecord", "r1"), constructionEdge("r1", "destNumber", "d1"));
    }
}
