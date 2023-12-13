/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.NoneVertex;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.lube.physical.planning.InnerJoin$;
import com.antgroup.openspg.reasoner.lube.physical.planning.JoinType;
import com.antgroup.openspg.reasoner.lube.physical.planning.LeftOuterJoin$;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author donghai.ydh
 * @version KgGraphJoinImpl.java, v 0.1 2023年10月18日 14:58 donghai.ydh
 */
public class KgGraphJoinImpl implements Serializable {
    private static final long serialVersionUID = -5771663962539687553L;
    private final JoinType            joinType;
    private final String              leftAlias;
    private final String              rightAlias;
    private final Map<String, String> otherJoinAliasMap        = new HashMap<>();
    private final Map<String, String> otherJoinAliasReverseMap = new HashMap<>();
    private final Long                pathLimit;

    private final scala.collection.immutable.Map<Var, Var> lhsSchemaMapping;
    private final scala.collection.immutable.Map<Var, Var> rhsSchemaMapping;
    private final Pattern                                  rightSchema;

    private final KgGraphRenameImpl rhsRenameImpl;

    private static final Set<JoinType> SUPPORTED_JOIN_TYPE = new HashSet<>();

    static {
        SUPPORTED_JOIN_TYPE.add(LeftOuterJoin$.MODULE$);
        SUPPORTED_JOIN_TYPE.add(InnerJoin$.MODULE$);
    }

    public KgGraphJoinImpl(JoinType joinType, scala.collection.immutable.List<Tuple2<String, String>> onAlias,
                           scala.collection.immutable.Map<Var, Var> lhsSchemaMapping,
                           scala.collection.immutable.Map<Var, Var> rhsSchemaMapping,
                           Pattern rightSchema,
                           Long pathLimit) {
        this.joinType = joinType;
        if (!SUPPORTED_JOIN_TYPE.contains(this.joinType)) {
            throw new NotImplementedException("not supported join type", null);
        }
        this.leftAlias = onAlias.head()._1();
        this.rightAlias = onAlias.head()._2();
        for (int i = 1; i < onAlias.size(); ++i) {
            Tuple2<String, String> tuple2 = onAlias.apply(i);
            otherJoinAliasMap.put(tuple2._1(), tuple2._2());
            otherJoinAliasReverseMap.put(tuple2._2(), tuple2._1());
        }
        this.rightSchema = rightSchema;
        this.pathLimit = pathLimit;
        this.lhsSchemaMapping = lhsSchemaMapping;
        this.rhsSchemaMapping = rhsSchemaMapping;
        this.rhsRenameImpl = new KgGraphRenameImpl(rhsSchemaMapping);
    }

    public List<KgGraph<IVertexId>> join(Collection<KgGraph<IVertexId>> left, Collection<KgGraph<IVertexId>> right) {
        long count = 0;
        List<KgGraph<IVertexId>> result = new ArrayList<>();
        for (KgGraph<IVertexId> leftKgGraph : left) {
            if (CollectionUtils.isEmpty(right)) {
                if (LeftOuterJoin$.MODULE$.equals(joinType)) {
                    leftJoinNone((KgGraphImpl) leftKgGraph);
                    result.add(leftKgGraph);
                    count++;
                }
                continue;
            }
            if (null != this.pathLimit && count > this.pathLimit) {
                continue;
            }
            for (KgGraph<IVertexId> rightKgGraph : right) {
                if (null != this.pathLimit && count > this.pathLimit) {
                    break;
                }
                if (!leftKgGraph.getVertex(leftAlias).get(0).getId().equals(rightKgGraph.getVertex(rightAlias).get(0).getId())) {
                    // id not equals
                    continue;
                }
                boolean otherVertexIdEquals = true;
                for (Map.Entry<String, String> entry : this.otherJoinAliasMap.entrySet()) {
                    if (!leftKgGraph.getVertex(entry.getKey()).get(0).getId().equals(
                            rightKgGraph.getVertex(entry.getValue()).get(0).getId())) {
                        // id not equals
                        otherVertexIdEquals = false;
                        break;
                    }
                }
                if (!otherVertexIdEquals) {
                    continue;
                }
                rightKgGraph = this.rhsRenameImpl.renameAndRemoveRoot(rightKgGraph, rightAlias);
                KgGraph<IVertexId> newKgGraph = new KgGraphImpl((KgGraphImpl) leftKgGraph);
                newKgGraph.merge(Lists.newArrayList(rightKgGraph), null);
                result.add(newKgGraph);
                count++;
            }
        }
        return result;
    }

    private void leftJoinNone(KgGraphImpl kgGraph) {
        Map<String, String> rightVertexAliasMap = new HashMap<>();
        Map<String, String> rightEdgeAliasMap = new HashMap<>();
        for (Map.Entry<Var, Var> entry : JavaConversions.mapAsJavaMap(this.rhsSchemaMapping).entrySet()) {
            if (entry.getKey() instanceof NodeVar) {
                rightVertexAliasMap.put(entry.getKey().name(), entry.getValue().name());
            } else if (entry.getKey() instanceof EdgeVar) {
                rightEdgeAliasMap.put(entry.getKey().name(), entry.getValue().name());
            } else {
                throw new RuntimeException("unsupported alias mapping type, " + entry.getKey().getClass().getName());
            }
        }
        for (Connection connection : RunnerUtil.getConnectionSet(this.rightSchema)) {
            Tuple2<String, IVertex<IVertexId, IProperty>> sourceTuple2 = getNoneVertex(connection.source(), rightVertexAliasMap, kgGraph);
            Tuple2<String, IVertex<IVertexId, IProperty>> targetTuple2 = getNoneVertex(connection.target(), rightVertexAliasMap, kgGraph);
            if (StringUtils.isNotEmpty(sourceTuple2._1())) {
                kgGraph.getAlias2VertexMap().put(sourceTuple2._1(), Sets.newHashSet(sourceTuple2._2()));
            }
            if (StringUtils.isNotEmpty(targetTuple2._1())) {
                kgGraph.getAlias2VertexMap().put(targetTuple2._1(), Sets.newHashSet(targetTuple2._2()));
            }
            kgGraph.getAlias2EdgeMap().put(rightEdgeAliasMap.get(connection.alias()),
                    Sets.newHashSet(new OptionalEdge<>(sourceTuple2._2().getId(), targetTuple2._2().getId())));
        }
    }

    private Tuple2<String, IVertex<IVertexId, IProperty>> getNoneVertex(String rightRowAlias, Map<String, String> rightVertexAliasMap,
                                                                        KgGraphImpl kgGraph) {
        if (rightRowAlias.equals(this.rightAlias)) {
            return new Tuple2<>(null, new NoneVertex<>(kgGraph.getVertex(this.leftAlias).get(0)));
        }
        if (otherJoinAliasReverseMap.containsKey(rightRowAlias)) {
            return new Tuple2<>(null, new NoneVertex<>(kgGraph.getVertex(otherJoinAliasReverseMap.get(rightRowAlias)).get(0)));
        }
        return new Tuple2<>(rightVertexAliasMap.get(rightRowAlias), new NoneVertex<>(kgGraph.getVertex(this.leftAlias).get(0)));
    }
}