/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.block.AddVertex;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
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
import java.util.TreeMap;

/**
 * @author donghai.ydh
 * @version ExtractRelationImpl.java, v 0.1 2023年06月29日 19:40 donghai.ydh
 */
public class ExtractVertexImpl implements Serializable {

private static final long serialVersionUID = 7788704894791547885L;
    private final Pattern             kgGraphSchema;
    private final Map<String, Object> initRuleContext;
    private final long                version;

    private final Map<String, List<String>> propertyRuleMap = new HashMap<>();
    private final String                    type;
    private final String                    alias;
    private final String                    taskId;

    /**
     * ddl add edge implement
     */
    public ExtractVertexImpl(AddVertex addVertex, Pattern kgGraphSchema, long version,
                             String taskId) {
        this.kgGraphSchema = kgGraphSchema;
        this.initRuleContext = RunnerUtil.getKgGraphInitContext(this.kgGraphSchema);
        this.version = version;
        this.taskId = taskId;

        Map<String, Expr> propertyExprMap = JavaConversions.mapAsJavaMap(addVertex.props());
        for (String propertyName : propertyExprMap.keySet()) {
            List<String> rule = WareHouseUtils.getRuleList(propertyExprMap.get(propertyName));
            propertyRuleMap.put(propertyName, rule);
        }
        this.type = addVertex.s().typeNames().iterator().next();
        this.alias = addVertex.s().alias();
    }

    /**
     * get ddl vertex
     */
    public IVertex<IVertexId, IProperty> extractVertex(KgGraph<IVertexId> kgGraph) {
        Map<String, Object> vertexProps = new HashMap<>();
        Map<String, Object> context = RunnerUtil.kgGraph2Context(this.initRuleContext, kgGraph);
        for (String propertyName : this.propertyRuleMap.keySet()) {
            List<String> ruleList = this.propertyRuleMap.get(propertyName);
            Object value = RuleRunner.getInstance().executeExpression(context, ruleList, this.taskId);
            vertexProps.put(propertyName, value);
        }
        IVertexId vertexId;
        if (vertexProps.containsKey("id")) {
            vertexId = IVertexId.from(String.valueOf(vertexProps.get("id")), this.type);
        } else {
            StringBuilder vertexIdSb = new StringBuilder();
            List<String> aliasList = Lists.newArrayList(kgGraph.getVertexAlias());
            aliasList.sort(String::compareTo);
            for (String vertexAlias : aliasList) {
                vertexIdSb.append(kgGraph.getVertex(vertexAlias).get(0).getId());
            }
            vertexId = IVertexId.from(vertexIdSb.toString(), this.type);
        }
        IVertex<IVertexId, IProperty> willAddedVertex = new Vertex<>(vertexId, getVertexProperty(vertexId, this.version, context));

        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
        alias2VertexMap.put(this.alias, Sets.newHashSet(willAddedVertex));
        KgGraph<IVertexId> newKgGraph = new KgGraphImpl(alias2VertexMap, new HashMap<>());
        kgGraph.merge(Lists.newArrayList(newKgGraph), null);
        return willAddedVertex;
    }

    private IProperty getVertexProperty(IVertexId vertexId, long version, Map<String, Object> context) {
        Map<String, TreeMap<Long, Object>> property = new HashMap<>();
        for (String propertyName : this.propertyRuleMap.keySet()) {
            List<String> ruleList = this.propertyRuleMap.get(propertyName);
            Object value = RuleRunner.getInstance().executeExpression(context, ruleList, this.taskId);
            TreeMap<Long, Object> versionValueMap = new TreeMap<>();
            versionValueMap.put(version, value);
            property.put(propertyName, versionValueMap);
        }
        return PropertyUtil.buildVertexProperty(vertexId, property);
    }

}