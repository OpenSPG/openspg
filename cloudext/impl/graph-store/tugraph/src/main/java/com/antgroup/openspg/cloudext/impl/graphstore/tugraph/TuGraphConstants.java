/*
 * Copyright 2023 OpenSPG Authors
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

  public static final String GRAPH_NAME = "graphName";

  public static final String KEY_LABEL = "label";
  public static final String KEY_PROPERTIES = "properties";
  public static final String KEY_TYPE = "type";
  public static final String KEY_KEY = "key";
  public static final String KEY_NODES = "nodes";
  public static final String KEY_EDGES = "edges";
  public static final String KEY_SRC = "src";
  public static final String KEY_SRC_TYPE = "srcType";
  public static final String KEY_SRC_KEY = "srcKey";
  public static final String KEY_DST = "dst";
  public static final String KEY_DST_TYPE = "dstType";
  public static final String KEY_DST_KEY = "dstKey";
  public static final String KEY_INNER_ID = "identity";

  public static String LABEL_TYPE_VERTEX = "vertex";
  public static String LABEL_TYPE_EDGE = "edge";

  public static String SCRIPT_NO_ALTER_TO_SCHEMA = "WITH 0 AS affected RETURN affected;";
}
