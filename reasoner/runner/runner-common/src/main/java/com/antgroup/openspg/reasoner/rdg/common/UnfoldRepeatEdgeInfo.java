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

import com.alibaba.fastjson.JSON;

import java.io.Serializable;


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