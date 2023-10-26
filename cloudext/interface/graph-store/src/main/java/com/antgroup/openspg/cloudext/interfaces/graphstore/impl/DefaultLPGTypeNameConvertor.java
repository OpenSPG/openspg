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

package com.antgroup.openspg.cloudext.interfaces.graphstore.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;


public class DefaultLPGTypeNameConvertor implements LPGTypeNameConvertor {

    private final static String DOT = ".";
    private final static String UNDERSCORE = "_";
    private final static String DOUBLE_UNDERSCORE = "__";

    @Override
    public String convertVertexTypeName(String vertexName) {
        return replace(vertexName, DOT, UNDERSCORE);
    }

    @Override
    public String convertEdgeTypeName(EdgeTypeName edgeTypeName) {
        return String.format(
            "%s%s%s%s%s",
            convertVertexTypeName(edgeTypeName.getStartVertexType()), DOUBLE_UNDERSCORE,
            edgeTypeName.getEdgeLabel(),
            DOUBLE_UNDERSCORE, convertVertexTypeName(edgeTypeName.getEndVertexType())
        );
    }

    @Override
    public String restoreVertexTypeName(String vertexName) {
        return replace(vertexName, UNDERSCORE, DOT);
    }

    @Override
    public EdgeTypeName restoreEdgeTypeName(String edgeName) {
        String[] splits = edgeName.split(DOUBLE_UNDERSCORE);
        if (splits.length != 3) {
            throw new IllegalArgumentException("illegal edgeName=" + edgeName);
        }
        return new EdgeTypeName(
            restoreVertexTypeName(splits[0]),
            splits[1],
            restoreVertexTypeName(splits[2])
        );
    }

    private String replace(String input, String oldStr, String newStr) {
        return input.replace(oldStr, newStr);
    }
}
