/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph.impl;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.impl.EdgeCombinationIterator.BuildEdgeIteratorInfo;
import com.antgroup.openspg.reasoner.kggraph.impl.EdgeCombinationIterator.Edge2VertexInfo;
import com.antgroup.openspg.reasoner.kggraph.impl.EdgeCombinationIterator.EdgeIterateInfo;
import com.antgroup.openspg.reasoner.kggraph.impl.EdgeCombinationIterator.IntersectVertexInfo;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author donghai.ydh
 * @version KgGraphSplitStaticParameters.java, v 0.1 2023年09月04日 19:52 donghai.ydh
 */
public class KgGraphSplitStaticParameters implements Serializable {
    private static final long serialVersionUID = 7532256372761127085L;
    private final List<EdgeIterateInfo> edgeIterateInfoList;
    private final Map<String, Integer>  edgeIterateOrderMap;

    private final Set<Connection>       needSplitEdgeSet;
    private final List<Set<Connection>> splitConnectedSubgraph;
    private final Set<String>           neighborAliasSet;
    private final Set<String>           connectedSubgraphVertexAliasSet;
    private final Set<String>           connectedSubgraphEdgeAliasSet;

    // split subgraph edge iterator info
    private final List<EdgeIterateInfo> subEdgeIterateInfoList;
    private final Map<String, Integer>  subEdgeIterateOrderMap;

    /**
     * 将KgGraph中重复调用的逻辑抽出来，在初始化中一次完成
     */
    public KgGraphSplitStaticParameters(Set<String> splitVertexAliases, Pattern schema) {
        this.edgeIterateInfoList = initEdgeIterateInfo(schema);
        this.edgeIterateOrderMap = initEdgeIterateOrderMap(this.edgeIterateInfoList);

        if (CollectionUtils.isNotEmpty(splitVertexAliases)) {
            this.needSplitEdgeSet = getNeedSplitEdgeSet(splitVertexAliases, schema);
            this.splitConnectedSubgraph = getSplitConnectedSubgraph(this.needSplitEdgeSet);
            if (this.splitConnectedSubgraph.size() > 1) {
                this.neighborAliasSet = null;
            } else {
                this.neighborAliasSet = getNeighborAliasSet(splitVertexAliases, schema);
            }
            Tuple2<Set<String>, Set<String>> setTuple2 = getVertexAndEdgeAliasSet(this.splitConnectedSubgraph.get(0));
            this.connectedSubgraphVertexAliasSet = setTuple2._1();
            this.connectedSubgraphEdgeAliasSet = setTuple2._2();
            this.subEdgeIterateInfoList = getSubEdgeIterateInfoList(this.splitConnectedSubgraph.get(0),
                    this.connectedSubgraphVertexAliasSet, schema);
            this.subEdgeIterateOrderMap = initEdgeIterateOrderMap(this.subEdgeIterateInfoList);
        } else {
            this.needSplitEdgeSet = null;
            this.splitConnectedSubgraph = null;
            this.neighborAliasSet = null;
            this.connectedSubgraphVertexAliasSet = null;
            this.connectedSubgraphEdgeAliasSet = null;
            this.subEdgeIterateInfoList = null;
            this.subEdgeIterateOrderMap = null;
        }
    }

    /**
     * Getter method for property <tt>needSplitEdgeSet</tt>.
     *
     * @return property value of needSplitEdgeSet
     */
    public Set<Connection> getNeedSplitEdgeSet() {
        return needSplitEdgeSet;
    }

    /**
     * Getter method for property <tt>splitConnectedSubgraph</tt>.
     *
     * @return property value of splitConnectedSubgraph
     */
    public List<Set<Connection>> getSplitConnectedSubgraph() {
        return splitConnectedSubgraph;
    }

    /**
     * Getter method for property <tt>edgeIterateInfoList</tt>.
     *
     * @return property value of edgeIterateInfoList
     */
    public List<EdgeIterateInfo> getEdgeIterateInfoList() {
        return edgeIterateInfoList;
    }

    /**
     * Getter method for property <tt>edgeIterateOrderMap</tt>.
     *
     * @return property value of edgeIterateOrderMap
     */
    public Map<String, Integer> getEdgeIterateOrderMap() {
        return edgeIterateOrderMap;
    }

    /**
     * Getter method for property <tt>vertexAliasSet</tt>.
     *
     * @return property value of vertexAliasSet
     */
    public Set<String> getConnectedSubgraphVertexAliasSet() {
        return connectedSubgraphVertexAliasSet;
    }

    /**
     * Getter method for property <tt>edgeAliasSet</tt>.
     *
     * @return property value of edgeAliasSet
     */
    public Set<String> getConnectedSubgraphEdgeAliasSet() {
        return connectedSubgraphEdgeAliasSet;
    }

    /**
     * Getter method for property <tt>subEdgeIterateInfoList</tt>.
     *
     * @return property value of subEdgeIterateInfoList
     */
    public List<EdgeIterateInfo> getSubEdgeIterateInfoList() {
        return subEdgeIterateInfoList;
    }

    /**
     * Getter method for property <tt>subEdgeIterateOrderMap</tt>.
     *
     * @return property value of subEdgeIterateOrderMap
     */
    public Map<String, Integer> getSubEdgeIterateOrderMap() {
        return subEdgeIterateOrderMap;
    }

    public boolean canDoSampleSplit(Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap) {
        if (null == this.neighborAliasSet) {
            return false;
        }
        for (String alias : this.neighborAliasSet) {
            if (alias2VertexMap.get(alias).size() > 1) {
                return false;
            }
        }
        return true;
    }

    private List<EdgeIterateInfo> getSubEdgeIterateInfoList(Set<Connection> subgraphEdgeSet,
                                                            Set<String> connectedSubgraphVertexAliasSet, Pattern schema) {
        Map<String, PatternElement> nodes = new HashMap<>();
        Map<String, Set<Connection>> topology = new HashMap<>();
        for (String vertexAlias : connectedSubgraphVertexAliasSet) {
            nodes.put(vertexAlias, schema.getNode(vertexAlias));
            for (Connection pc : subgraphEdgeSet) {
                if (pc.source().equals(vertexAlias)) {
                    Set<Connection> sets = topology.computeIfAbsent(vertexAlias, k -> new HashSet<>());
                    sets.add(pc);
                }
            }
        }
        Map<String, scala.collection.immutable.Set<Connection>> topologyImmutableSet = new HashMap<>();
        for (Map.Entry<String, Set<Connection>> entry : topology.entrySet()) {
            topologyImmutableSet.put(entry.getKey(), JavaConversions.asScalaSet(entry.getValue()).toSet());
        }

        Pattern subSchema = new PartialGraphPattern("",
                JavaConversions.mapAsScalaMap(nodes).toMap(scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topologyImmutableSet));
        return initEdgeIterateInfo(subSchema);
    }

    private static Set<String> getNeighborAliasSet(Set<String> vertexAliases, Pattern schema) {
        Set<String> neighborAliasSet = new HashSet<>();
        for (Connection pc : RunnerUtil.getConnectionSet(schema)) {
            for (String alias : vertexAliases) {
                String neighborAlias = RunnerUtil.getNeighborAlias(alias, pc);
                if (StringUtils.isNotEmpty(neighborAlias)) {
                    if (vertexAliases.contains(neighborAlias)) {
                        return null;
                    }
                    neighborAliasSet.add(neighborAlias);
                }
            }
        }
        return neighborAliasSet;
    }

    /**
     * 获取边列表所覆盖的点和边的alias
     */
    private static Tuple2<Set<String>, Set<String>> getVertexAndEdgeAliasSet(Set<Connection> connectedEdgeSet) {
        Set<String> vertexAlias = new HashSet<>();
        Set<String> edgeAlias = new HashSet<>();
        for (Connection pc : connectedEdgeSet) {
            vertexAlias.add(pc.source());
            vertexAlias.add(pc.target());
            edgeAlias.add(pc.alias());
        }
        return new Tuple2<>(vertexAlias, edgeAlias);
    }

    /**
     * 将需要划分的PatternConnectionSet, 切分为几个连通的PatternConnectionSet
     */
    private List<Set<Connection>> getSplitConnectedSubgraph(Set<Connection> needSplitEdgeSet) {
        needSplitEdgeSet = Sets.newHashSet(needSplitEdgeSet);
        List<Set<Connection>> result = new ArrayList<>();
        while (!needSplitEdgeSet.isEmpty()) {
            Set<Connection> connectedEdgeSet = new HashSet<>();
            Set<String> touchedVertexSet = new HashSet<>();

            boolean hasConnectedEdge = true;

            while (hasConnectedEdge) {
                boolean findOneConnectedEdge = false;
                Iterator<Connection> it = needSplitEdgeSet.iterator();
                while (it.hasNext()) {
                    Connection pc = it.next();
                    if ((touchedVertexSet.isEmpty())
                            || (touchedVertexSet.contains(pc.source()) || touchedVertexSet.contains(pc.target()))) {
                        findOneConnectedEdge = true;
                        connectedEdgeSet.add(pc);
                        touchedVertexSet.add(pc.source());
                        touchedVertexSet.add(pc.target());
                        it.remove();
                    }
                }
                if (!findOneConnectedEdge) {
                    hasConnectedEdge = false;
                }
            }
            result.add(connectedEdgeSet);
        }
        return result;
    }

    private static Set<Connection> getNeedSplitEdgeSet(Set<String> vertexAliases, Pattern schema) {
        Set<Connection> needSplitEdgeSet = new HashSet<>();
        for (Connection pc : RunnerUtil.getConnectionSet(schema)) {
            if (vertexAliases.contains(pc.source()) || vertexAliases.contains(pc.target())) {
                needSplitEdgeSet.add(pc);
            }
        }
        return needSplitEdgeSet;
    }

    private static Map<String, Integer> initEdgeIterateOrderMap(List<EdgeIterateInfo> edgeIterateInfoList) {
        Map<String, Integer> edgeIterateOrderMap = new HashMap<>();
        for (int i = 0; i < edgeIterateInfoList.size(); ++i) {
            EdgeIterateInfo edgeIterateInfo = edgeIterateInfoList.get(i);
            edgeIterateOrderMap.put(edgeIterateInfo.getEdgeAlias(), i);
        }
        return edgeIterateOrderMap;
    }

    private static List<EdgeIterateInfo> initEdgeIterateInfo(Pattern schema) {
        List<EdgeIterateInfo> edgeIterateInfoList = new ArrayList<>();
        List<Connection> patternConnectionList = Lists.newArrayList(RunnerUtil.getConnectionSet(schema));
        Map<String, Connection> patternConnectionMap = new HashMap<>();
        for (Connection pc : patternConnectionList) {
            patternConnectionMap.put(pc.alias(), pc);
        }
        // 先确定迭代Edge的顺序
        // 这里的逻辑是确保当前Edge与之前迭代过的Edge是有共同Vertex的
        Set<String> touchedVertexAliasSet = new HashSet<>();
        while (!patternConnectionList.isEmpty()) {
            boolean findOne = false;
            Iterator<Connection> it = patternConnectionList.iterator();
            while (it.hasNext()) {
                Connection pc = it.next();
                if (touchedVertexAliasSet.contains(pc.source()) || touchedVertexAliasSet.contains(pc.target())) {
                    EdgeIterateInfo edgeIterateInfo = new EdgeIterateInfo(pc);
                    edgeIterateInfoList.add(edgeIterateInfo);

                    touchedVertexAliasSet.add(pc.source());
                    touchedVertexAliasSet.add(pc.target());

                    it.remove();
                    findOne = true;
                    break;
                }
            }
            if (!findOne) {
                Connection target = null;
                if (CollectionUtils.isEmpty(edgeIterateInfoList)) {
                    String edgeAlias = patternConnectionList.get(0).alias();
                    for (Connection pc : patternConnectionList) {
                        if (pc.alias().equals(edgeAlias)) {
                            target = pc;
                            break;
                        }
                    }
                    patternConnectionList.remove(target);
                } else {
                    target = patternConnectionList.remove(0);
                }
                EdgeIterateInfo edgeIterateInfo = new EdgeIterateInfo(target);
                edgeIterateInfoList.add(edgeIterateInfo);

                touchedVertexAliasSet.add(target.source());
                touchedVertexAliasSet.add(target.target());
            }
        }

        //计算边相交情况
        Map<String, List<Edge2VertexInfo>> touchedVertexAlias2EdgeMap = new HashMap<>();
        for (int i = 0; i < edgeIterateInfoList.size(); ++i) {
            EdgeIterateInfo edgeIterateInfo = edgeIterateInfoList.get(i);
            List<Edge2VertexInfo> sourceVertexInfoList = touchedVertexAlias2EdgeMap.getOrDefault(
                    edgeIterateInfo.getSourceAlias(), new ArrayList<>());
            List<Edge2VertexInfo> targetVertexInfoList = touchedVertexAlias2EdgeMap.getOrDefault(
                    edgeIterateInfo.getTargetAlias(), new ArrayList<>());

            if (i > 0) {
                Edge2VertexInfo queryKey;
                boolean indexSource;
                if (!sourceVertexInfoList.isEmpty()) {
                    queryKey = sourceVertexInfoList.remove(0);
                    indexSource = true;
                } else {
                    queryKey = targetVertexInfoList.remove(0);
                    indexSource = false;
                }

                BuildEdgeIteratorInfo buildEdgeIteratorInfo = new BuildEdgeIteratorInfo(
                        edgeIterateInfo.getEdgeAlias(), indexSource, queryKey);
                edgeIterateInfo.setBuildEdgeIteratorInfo(buildEdgeIteratorInfo);
            }

            List<IntersectVertexInfo> intersectInfoList = new ArrayList<>();
            for (Edge2VertexInfo edge2VertexInfo : sourceVertexInfoList) {
                intersectInfoList.add(new IntersectVertexInfo(true, edge2VertexInfo));
            }
            for (Edge2VertexInfo edge2VertexInfo : targetVertexInfoList) {
                intersectInfoList.add(new IntersectVertexInfo(false, edge2VertexInfo));
            }
            edgeIterateInfo.setIntersectInfoList(intersectInfoList);

            List<Edge2VertexInfo> sourceVertexEdgeList = touchedVertexAlias2EdgeMap.computeIfAbsent(
                    edgeIterateInfo.getSourceAlias(), k -> new ArrayList<>());
            sourceVertexEdgeList.add(new Edge2VertexInfo(i, edgeIterateInfo.getEdgeAlias(), true));
            List<Edge2VertexInfo> targetVertexEdgeList = touchedVertexAlias2EdgeMap.computeIfAbsent(
                    edgeIterateInfo.getTargetAlias(), k -> new ArrayList<>());
            targetVertexEdgeList.add(new Edge2VertexInfo(i, edgeIterateInfo.getEdgeAlias(), false));
        }

        // init duplicate vertex check map
        for (int i = 1; i < edgeIterateInfoList.size(); ++i) {
            EdgeIterateInfo nowEdgeIterateInfo = edgeIterateInfoList.get(i);
            Connection nowPc = patternConnectionMap.get(nowEdgeIterateInfo.getEdgeAlias());
            String nowPcSource = nowPc.source();
            String nowPcTarget = nowPc.target();
            Set<String> sourceCheckAliasSet = new HashSet<>();
            Set<String> targetCheckAliasSet = new HashSet<>();
            for (int j = 0; j < i; ++j) {
                EdgeIterateInfo checkEdgeIterateInfo = edgeIterateInfoList.get(j);
                Connection checkPc = patternConnectionMap.get(checkEdgeIterateInfo.getEdgeAlias());
                String checkPcSource = checkPc.source();
                String checkPcTarget = checkPc.target();
                if (!sourceCheckAliasSet.contains(checkPcSource) && hasSameVertexType(schema, nowPcSource, checkPcSource)) {
                    nowEdgeIterateInfo.getDuplicateVertexCheck().put(j, new Tuple2<>(true, true));
                }
                sourceCheckAliasSet.add(checkPcSource);
                if (!sourceCheckAliasSet.contains(checkPcTarget) && hasSameVertexType(schema, nowPcSource, checkPcTarget)) {
                    nowEdgeIterateInfo.getDuplicateVertexCheck().put(j, new Tuple2<>(true, false));
                }
                sourceCheckAliasSet.add(checkPcTarget);
                if (!targetCheckAliasSet.contains(checkPcSource) && hasSameVertexType(schema, nowPcTarget, checkPcSource)) {
                    nowEdgeIterateInfo.getDuplicateVertexCheck().put(j, new Tuple2<>(false, true));
                }
                targetCheckAliasSet.add(checkPcSource);
                if (!targetCheckAliasSet.contains(checkPcTarget) && hasSameVertexType(schema, nowPcTarget, checkPcTarget)) {
                    nowEdgeIterateInfo.getDuplicateVertexCheck().put(j, new Tuple2<>(false, false));
                }
                targetCheckAliasSet.add(checkPcTarget);
            }
        }
        return edgeIterateInfoList;
    }

    private static boolean hasSameVertexType(Pattern schema, String alias1, String alias2) {
        if (alias1.equals(alias2)) {
            return false;
        }
        Set<String> nowTypeSet = Sets.newHashSet(JavaConversions.setAsJavaSet(schema.getNode(alias1).typeNames()));
        Set<String> checkTypeSet = Sets.newHashSet(JavaConversions.setAsJavaSet(schema.getNode(alias2).typeNames()));
        nowTypeSet.retainAll(checkTypeSet);
        // same type，need check
        return !nowTypeSet.isEmpty();
    }

}