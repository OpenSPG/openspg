/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author donghai.ydh
 * @version UnfoldRepeatEdgeInfo.java, v 0.1 2023年11月14日 20:07 donghai.ydh
 */
public class UnfoldRepeatEdgeInfo implements Serializable {
    private static final long serialVersionUID = 7558197193435125339L;
    private final String edgeAlias;
    private final String foldVertexAlias;
    private final String anchorVertexAlias;
    private final int    lower;

    public UnfoldRepeatEdgeInfo(String edgeAlias, String foldVertexAlias, String anchorVertexAlias, int lower) {
        this.edgeAlias = edgeAlias;
        this.foldVertexAlias = foldVertexAlias;
        this.anchorVertexAlias = anchorVertexAlias;
        this.lower = lower;
    }

    /**
     * Getter method for property <tt>edgeAlias</tt>.
     *
     * @return property value of edgeAlias
     */
    public String getEdgeAlias() {
        return edgeAlias;
    }

    /**
     * Getter method for property <tt>vertexAlias</tt>.
     *
     * @return property value of vertexAlias
     */
    public String getFoldVertexAlias() {
        return foldVertexAlias;
    }

    /**
     * Getter method for property <tt>lower</tt>.
     *
     * @return property value of lower
     */
    public int getLower() {
        return lower;
    }

    /**
     * Getter method for property <tt>anchorVertexAlias</tt>.
     *
     * @return property value of anchorVertexAlias
     */
    public String getAnchorVertexAlias() {
        return anchorVertexAlias;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}