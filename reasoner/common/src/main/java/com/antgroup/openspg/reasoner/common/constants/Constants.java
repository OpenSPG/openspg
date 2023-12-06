/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.constants;

/**
 * @author chengqiang.cq
 * @version $Id: Constants.java, v 0.1 2023-05-16 16:57 chengqiang.cq Exp $$
 */
public class Constants {
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

  /** start label config */
  public static final String START_LABEL = "start_label";

  /** force kgreasoner output string */
  public static final String KG_REASONER_OUTPUT_COLUMN_FORCE_STRING =
      "kg.reasoner.output.column.force.string";

  public static final String OPTIONAL_EDGE_FLAG = "__optional_edge__";
  public static final String NONE_VERTEX_FLAG = "__none_vertex__";
  public static final String MIRROR_VERTEX_FLAG = "__mirror_vertex__";
}
