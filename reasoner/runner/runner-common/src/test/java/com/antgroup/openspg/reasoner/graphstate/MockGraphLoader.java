/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
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

/**
 * @author peilong.zpl
 * @version $Id: MockGraphLoader.java, v 0.1 2023-08-29 17:59 peilong.zpl Exp $$
 */
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