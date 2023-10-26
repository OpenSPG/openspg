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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph;


public class TuGraphConstants {

    public static final String TUGRAPH_EDGE_INTERNAL_ID = "__eid__";

    public final static String GRAPH_NAME = "graphName";

    public final static String KEY_LABEL = "label";
    public final static String KEY_PROPERTIES = "properties";
    public final static String KEY_TYPE = "type";
    public final static String KEY_KEY = "key";
    public final static String KEY_NODES = "nodes";
    public final static String KEY_EDGES = "edges";
    public final static String KEY_SRC = "src";
    public final static String KEY_SRC_TYPE = "srcType";
    public final static String KEY_SRC_KEY = "srcKey";
    public final static String KEY_DST = "dst";
    public final static String KEY_DST_TYPE = "dstType";
    public final static String KEY_DST_KEY = "dstKey";
    public final static String KEY_INNER_ID = "identity";

    public static String LABEL_TYPE_VERTEX = "vertex";
    public static String LABEL_TYPE_EDGE = "edge";

    public static String SCRIPT_NO_ALTER_TO_SCHEMA = "WITH 0 AS affected RETURN affected;";
}
