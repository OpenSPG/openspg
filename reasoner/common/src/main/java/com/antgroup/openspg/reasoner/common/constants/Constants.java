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

package com.antgroup.openspg.reasoner.common.constants;

import com.google.common.collect.Sets;
import java.util.Set;

public class Constants {

  public static final String CONTEXT_TYPE = "__type__";
  public static final String CONTEXT_ALIAS = "__alias__";

  public static final String CONTEXT_LABEL = "__label__";
  /** edge from id key */
  public static final String EDGE_FROM_ID_KEY = "__from_id__";
  /** edge to id key */
  public static final String EDGE_TO_ID_KEY = "__to_id__";

  /** node id key */
  public static final String NODE_ID_KEY = "id";

  /** vertex internal id */
  public static final String VERTEX_INTERNAL_ID_KEY = "__id__";

  /** edge from internal id */
  public static final String EDGE_FROM_INTERNAL_ID_KEY = "__from_internal_id__";

  public static final String EDGE_FROM_ID_TYPE_KEY = "__from_id_type__";

  /** edge to internal id */
  public static final String EDGE_TO_INTERNAL_ID_KEY = "__to_internal_id__";

  public static final String EDGE_TO_ID_TYPE_KEY = "__to_id_type__";

  /** parse runtime params */
  public static final String START_ALIAS = "start_alias";

  public static final String SPG_REASONER_LUBE_SUBQUERY_ENABLE =
      "spg.reasoner.lube.subquery.enable";
  public static final String SPG_REASONER_MULTI_VERSION_ENABLE =
      "spg.reasoner.multi.version.enable";

  /** enable plan pretty print debug logger */
  public static final String SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE =
      "spg.reasoner.plan.pretty.print.logger.enable";

  /** enable strict mode in thinker during rule engine execution, default is false */
  public static final String SPG_REASONER_THINKER_STRICT = "spg.reasoner.thinker.strict";

  /** start label config */
  public static final String START_LABEL = "start_label";

  /** force kgreasoner output string */
  public static final String KG_REASONER_OUTPUT_COLUMN_FORCE_STRING =
      "kg.reasoner.output.column.force.string";

  public static final String OPTIONAL_EDGE_FLAG = "__optional_edge__";
  public static final String REPEAT_EDGE_FLAG = "__repeat_edge__";
  public static final String NONE_VERTEX_FLAG = "__none_vertex__";
  public static final String MIRROR_VERTEX_FLAG = "__mirror_vertex__";

  /** property */
  public static final String PROPERTY_JSON_KEY = "__property_json__";

  public static final String GET_PATH_KEY = "__path__";
  /** load all property */
  public static final String CARRY_ALL_FLAG = "__carry_all__";

  /** ddl edge version property key */
  public static final String DDL_EDGE_VERSION_KEY = "version";

  /** hypernym edge type */
  public static final Set<String> CONCEPT_HYPERNYM_EDGE_TYPE_SET =
      Sets.newHashSet("isA", "locateAt");

  /** open hypernym predicate type */
  public static final Set<String> OPEN_CONCEPT_HYPERNYM_PREDICATE =
      Sets.newHashSet("isA", "locateAt", "mannerOf");

  public static final String CONCEPT_EDGE_EXPAND_FUNC_NAME = "concept_edge_expand";

  /** allow throw exception in udf */
  public static final String ALLOW_UDF_EXCEPTION = "allowUDFThrowException";
}
