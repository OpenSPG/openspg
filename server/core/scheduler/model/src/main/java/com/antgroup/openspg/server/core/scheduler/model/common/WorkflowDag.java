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

package com.antgroup.openspg.server.core.scheduler.model.common;

import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Workflow Dag
 *
 * @author yangjin
 * @Title: WorkflowDag.java
 * @Description:
 */
public class WorkflowDag {

    /**
     * dag nodes List
     */
    private List<Node> nodes = Collections.emptyList();

    /**
     * dag edges List
     */
    private List<Edge> edges = Collections.emptyList();

    /**
     * dag extend
     */
    private String extend;

    /**
     * get Nodes By Type
     *
     * @param nodeType
     * @return
     */
    @JSONField(serialize = false)
    public List<Node> getNodesByType(String nodeType) {
        List<Node> nodes = Lists.newArrayList();
        if (nodeType == null) {
            return nodes;
        }
        for (Node node : this.nodes) {
            if (node == null) {
                continue;
            }
            if (nodeType.equals(node.getType())) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * get node by id
     *
     * @param nodeId
     * @return
     */
    @JSONField(serialize = false)
    public Node getNode(String nodeId) {
        for (Node node : nodes) {
            if (node.getId().equals(nodeId)) {
                return node;
            }
        }
        return null;
    }

    /**
     * get node by id list
     *
     * @param idList
     * @return
     */
    @JSONField(serialize = false)
    public List<Node> getNode(List<String> idList) {
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isEmpty(idList)) {
            return nodes;
        }
        for (Node node : this.nodes) {
            if (idList.contains(node.getId())) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * get Next Nodes
     *
     * @param nodeId
     * @return
     */
    @JSONField(serialize = false)
    public List<Node> getNextNodes(String nodeId) {
        List<String> idList = Lists.newArrayList();
        for (Edge edge : edges) {
            if (edge.getFrom().equals(nodeId)) {
                idList.add(edge.getTo());
            }
        }
        return getNode(idList);
    }

    /**
     * get Pre Nodes
     *
     * @param nodeId
     * @return
     */
    @JSONField(serialize = false)
    public List<Node> getPreNodes(String nodeId) {
        List<String> idList = Lists.newArrayList();
        for (Edge edge : edges) {
            if (edge.getTo().equals(nodeId)) {
                idList.add(edge.getFrom());
            }
        }
        return getNode(idList);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static class Node<T> {
        /**
         * id
         */
        private String id;

        /**
         * type
         */
        private String type;

        /**
         * name
         */
        private String name;

        /**
         * X coordinate
         */
        private Long x;

        /**
         * Y coordinate
         */
        private Long y;

        /**
         * properties
         */
        private T properties;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getX() {
            return x;
        }

        public void setX(Long x) {
            this.x = x;
        }

        public Long getY() {
            return y;
        }

        public void setY(Long y) {
            this.y = y;
        }

        public T getProperties() {
            return properties;
        }

        public void setProperties(T properties) {
            this.properties = properties;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    public static class Edge {
        /**
         * from id
         */
        private String from;

        /**
         * to id
         */
        private String to;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

}
