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
package com.antgroup.openspg.reasoner.graphstate;

import java.util.Iterator;

import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.warehouse.common.AbstractGraphLoader;
import com.antgroup.openspg.reasoner.warehouse.common.VertexSubGraph;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig;
import org.jetbrains.annotations.NotNull;


public class MockGraphLoader extends AbstractGraphLoader {
    /**
     * loader
     * @param graphLoaderConfig
     */
    public MockGraphLoader(GraphLoaderConfig graphLoaderConfig) {
        super(graphLoaderConfig);
    }

    @Override
    public void close() throws Exception {

    }

    @NotNull
    @Override public Iterator<VertexSubGraph> iterator() {
        return null;
    }

    @Override
    public VertexSubGraph queryOneHotGraphState(IVertexId vertexId) {
        IVertexId mockVertex = IVertexId.from("abc", "Test");
        if (mockVertex.equals(vertexId)) {
            return new VertexSubGraph(new Vertex<>(mockVertex, new VertexVersionProperty()));
        }
        return null;
    }
}