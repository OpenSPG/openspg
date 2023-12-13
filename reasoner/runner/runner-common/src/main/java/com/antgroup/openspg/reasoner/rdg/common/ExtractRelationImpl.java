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
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.block.AddPredicate;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.pattern.Element;
import com.antgroup.openspg.reasoner.lube.common.pattern.EntityElement;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PatternElement;
import com.antgroup.openspg.reasoner.rule.RuleRunner;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import scala.collection.JavaConversions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ExtractRelationImpl implements Serializable {

private static final long serialVersionUID = 3442064493302533370L;
    private final AddPredicate addPredicate;

    private final String    predicate;
    private final Direction direction;
    private final boolean   withReverseEdge;

    private final Pattern             kgGraphSchema;
    private final Map<String, Object> initRuleContext;
    private final long                version;

    private final Map<String, List<String>> propertyRuleMap = new HashMap<>();

    private final String taskId;

    private final PatternElement sourceElement;
    private final EntityElement  targetEntityElement;
    private final PatternElement targetPatternElement;

    /**
     * ddl add edge implement
     */
    public ExtractRelationImpl(AddPredicate addPredicate, Pattern kgGraphSchema, long version,
                               String taskId) {
        this.addPredicate = addPredicate;
        this.kgGraphSchema = kgGraphSchema;
        this.initRuleContext = RunnerUtil.getKgGraphInitContext(this.kgGraphSchema);
        this.version = version;
        this.taskId = taskId;

        this.predicate = this.addPredicate.predicate().label();
        this.direction = this.addPredicate.predicate().direction();
        this.withReverseEdge = !(addPredicate.predicate().target() instanceof EntityElement);

        Map<String, Expr> propertyExprMap = JavaConversions.mapAsJavaMap(addPredicate.predicate().fields());
        for (String propertyName : propertyExprMap.keySet()) {
            List<String> rule = WareHouseUtils.getRuleList(propertyExprMap.get(propertyName));
            this.propertyRuleMap.put(propertyName, rule);
        }

        PatternElement sourceElement = (PatternElement) addPredicate.predicate().source();
        Element te = addPredicate.predicate().target();
        EntityElement targetEntityElement = null;
        PatternElement targetPatternElement = null;
        if (te instanceof EntityElement) {
            targetEntityElement = (EntityElement) te;
            this.propertyRuleMap.put(Constants.EDGE_TO_ID_KEY, Lists.newArrayList("'" + targetEntityElement.id() + "'"));
        } else {
            targetPatternElement = (PatternElement) te;
            this.propertyRuleMap.put(Constants.EDGE_TO_ID_KEY, Lists.newArrayList(targetPatternElement.alias() + ".id"));
        }
        this.propertyRuleMap.put(Constants.EDGE_FROM_ID_KEY, Lists.newArrayList(sourceElement.alias() + ".id"));

        this.sourceElement = sourceElement;
        this.targetEntityElement = targetEntityElement;
        this.targetPatternElement = targetPatternElement;
    }

    /**
     * get ddl edges
     */
    public IEdge<IVertexId, IProperty> extractEdge(KgGraph<IVertexId> kgGraph) {
        IVertexId s = kgGraph.getVertex(sourceElement.alias()).get(0).getId();
        IVertexId o = getTargetVertexId(targetEntityElement, targetPatternElement, kgGraph);

        IEdge<IVertexId, IProperty> willAddedEdge = new Edge<>(s, o,
                getEdgeProperty(kgGraph),
                version, direction, getEdgeType(s, o));

        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
        alias2EdgeMap.put(this.addPredicate.predicate().alias(), Sets.newHashSet(willAddedEdge));
        KgGraph<IVertexId> newKgGraph = new KgGraphImpl(new HashMap<>(), alias2EdgeMap);
        kgGraph.merge(Lists.newArrayList(newKgGraph), null);

        return willAddedEdge;
    }

    private IProperty getEdgeProperty(KgGraph<IVertexId> kgGraph) {
        Map<String, Object> edgeProps = new HashMap<>();
        Map<String, Object> context = RunnerUtil.kgGraph2Context(this.initRuleContext, kgGraph);
        for (String propertyName : this.propertyRuleMap.keySet()) {
            List<String> ruleList = this.propertyRuleMap.get(propertyName);
            Object value = RuleRunner.getInstance().executeExpression(context, ruleList, this.taskId);
            edgeProps.put(propertyName, value);
        }
        return new EdgeProperty(edgeProps);
    }

    private IVertexId getTargetVertexId(EntityElement entityElement, PatternElement patternElement, KgGraph<IVertexId> path) {
        if (null != entityElement) {
            return IVertexId.from(String.valueOf(entityElement.id()), entityElement.label());
        }
        return path.getVertex(patternElement.alias()).get(0).getId();
    }

    private String getEdgeType(IVertexId s, IVertexId o) {
        SPO spo = new SPO(s.getType(), predicate, o.getType());
        return spo.toString();
    }

    /**
     * reverse edge
     */
    public IEdge<IVertexId, IProperty> createReverseEdge(IEdge<IVertexId, IProperty> edge) {
        return new Edge<>(edge.getTargetId(), edge.getSourceId(),
                edge.getValue(),
                edge.getVersion(),
                Utils.reverseDirection(edge.getDirection()),
                edge.getType());
    }

    /**
     * need add reverse edge
     */
    public boolean withReverseEdge() {
        return this.withReverseEdge;
    }

    /**
     * Getter method for property <tt>predicate</tt>.
     *
     * @return property value of predicate
     */
    public String getPredicate() {
        return predicate;
    }

}