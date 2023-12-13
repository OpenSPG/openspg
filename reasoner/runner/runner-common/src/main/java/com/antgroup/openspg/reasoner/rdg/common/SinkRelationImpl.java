/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import scala.Tuple2;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author donghai.ydh
 * @version SinkRelationImpl.java, v 0.1 2023年07月24日 10:32 donghai.ydh
 */
@Slf4j(topic = "userlogger")
public class SinkRelationImpl {
    private final GraphState<IVertexId> graphState;
    private final int                   taskIndex;

    /**
     * sink edge implement
     */
    public SinkRelationImpl(GraphState<IVertexId> graphState, int taskIndex) {
        this.graphState = graphState;
        this.taskIndex = taskIndex;
    }

    /**
     * write to graph state and return number
     */
    public long sink(Collection<IEdge<IVertexId, IProperty>> values) {
        long sinkCount = 0;
        log.info("SinkRelation,start,index=" + taskIndex + ",size=" + values.size());
        Map<IVertexId, Tuple2<Set<IEdge<IVertexId, IProperty>>, Set<IEdge<IVertexId, IProperty>>>> edgeMap = new HashMap<>();
        for (IEdge<IVertexId, IProperty> edge : values) {
            Tuple2<Set<IEdge<IVertexId, IProperty>>, Set<IEdge<IVertexId, IProperty>>> edgeListTuple2 =
                    edgeMap.computeIfAbsent(edge.getSourceId(), k -> new Tuple2<>(new HashSet<>(), new HashSet<>()));
            if (Direction.IN.equals(edge.getDirection())) {
                edgeListTuple2._1().add(edge);
            } else {
                edgeListTuple2._2().add(edge);
            }
        }

        for (Map.Entry<IVertexId, Tuple2<Set<IEdge<IVertexId, IProperty>>, Set<IEdge<IVertexId, IProperty>>>> entry :
                edgeMap.entrySet()) {
            IVertexId vertexId = entry.getKey();
            Set<IEdge<IVertexId, IProperty>> inEdgeList = entry.getValue()._1();
            Set<IEdge<IVertexId, IProperty>> outEdgeList = entry.getValue()._2();
            this.graphState.addEdges(vertexId, Lists.newArrayList(inEdgeList), Lists.newArrayList(outEdgeList));
            sinkCount++;
            if (Utils.randomLog()) {
                log.info("SinkRelation,v=" + vertexId + ",inEdge=" + inEdgeList.size() + ",outEdge=" + outEdgeList
                        + ",sinkCount=" + sinkCount);
            }
        }
        return sinkCount;
    }
}