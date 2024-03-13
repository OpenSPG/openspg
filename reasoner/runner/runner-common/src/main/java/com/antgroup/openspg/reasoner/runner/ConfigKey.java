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

package com.antgroup.openspg.reasoner.runner;

public class ConfigKey {

  /** dsl content */
  public static final String KG_REASONER_DSL = "kg.reasoner.dsl";

  /** dsl task parameters */
  public static final String KG_REASONER_PARAMS = "kg.reasoner.params";

  /** rayag worker num */
  public static final String RAYAG_WORKER_NUM = "rayag.worker.num";

  /** rayag driver num */
  public static final String KG_REASONER_DRIVER_NUM = "kg.reasoner.driver.num";

  /** rayag source num */
  public static final String KG_REASONER_SOURCE_NUM = "kg.reasoner.source.num";

  /** source function */
  public static final String KG_REASONER_STREAMING_SOURCE_FUNCTION =
      "kg.reasoner.streaming.source.function";

  /** driver class name, only for olap */
  public static final String KG_REASONER_DRIVER_CLASS_NAME = "kg.reasoner.driver.class.name";

  /** kg reasoner msg broker config */
  public static final String KG_REASONER_MSG_BROKER_CONFIG = "kg.reasoner.msg.broker.config";

  /** kg reasoner output table config */
  public static final String KG_REASONER_OUTPUT_TABLE_CONFIG = "output.table.config";

  /** sink to file */
  public static final String KG_REASONER_SINK_FILE = "kg.reasoner.sink.file";

  /** sink table info */
  public static final String KG_REASONER_SINK_TABLE_INFO = "kg.reasoner.sink.table.info";

  /** progress path */
  public static final String KG_REASONER_PROGRESS_PATH = "engine.progress.path";

  /** start id list */
  public static final String KG_REASONER_START_ID_LIST = "kg.reasoner.start.id.list";

  /** kg reasoner result server config */
  public static final String KG_REASONER_RESULT_SERVER = "kg.reasoner.result.server";

  /** kg reasoner data namespace server config */
  public static final String KG_REASONER_NAMESPACE_SERVER = "kg.reasoner.namespace.server";

  /** kg reasoner schema server token config */
  public static final String KG_REASONER_SCHEMA_SERVER_TOKEN = "kg.reasoner.schema.server.token";

  /** preload enable */
  public static final String KG_REASONER_PRE_LOAD_ENABLE = "kg.reasoner.preload.enable";

  /** query dfs config */
  public static final String KG_REASONER_QUERY_DFS_CONFIG = "kg.reasoner.query.dfs.config";

  /** kg reasoner olap env config */
  public static final String KG_REASONER_OLAP_ENV = "kg.reasoner.olap.env";

  /** kg reasoner olap init project */
  public static final String KG_REASONER_OLAP_INIT_PROJECT = "kg.reasoner.olap.init.project";

  /** kg reasoner olap exec topic config */
  public static final String KG_REASONER_OLAP_EXEC_TOPIC = "kg.reasoner.olap.exec.topic";

  /** kg reasoner olap task handler config */
  public static final String KG_REASONER_OLAP_TASK_HANDLER = "kg.reasoner.olap.task.handler";

  /** set catalog */
  public static final String KG_REASONER_CATALOG = "kg.reasoner.catalog";

  /** set graph load config */
  public static final String KG_REASONER_GRAPH_LOAD_CONFIG = "kg.reasoner.graph.load.config";

  /** set graph load kgstate connection */
  public static final String KG_REASONER_GRAPH_KGSTATE_CONNECTION =
      "kg.reasoner.graph.load.kgstate.connection";

  /** set kgstate schema url */
  public static final String KG_REASONER_GRAPH_KGSTATE_SCHEMA_URL =
      "kg.reasoner.graph.load.kgstate.schema.url";

  /** enable cache project */
  public static final String KG_REASONER_PLAN_CACHE_PROJECT_ENABLE =
      "kg.reasoner.plan_cache_project_enable";

  /** set graph data */
  public static final String KG_REASONER_GRAPH_DATA = "kg.reasoner.graph.data";

  /** set binary property, default is true */
  public static final String KG_REASONER_BINARY_PROPERTY = "kg.reasoner.binary.property";

  /** mock graph data */
  public static final String KG_REASONER_MOCK_GRAPH_DATA = "kg.reasoner.mock.graph.data";

  /** expect batch num */
  public static final String KG_REASONER_EXPECT_BATCH_NUM = "kg.reasoner.expect.batch.num";

  /** output graph config */
  public static final String KG_REASONER_OUTPUT_GRAPH = "kg.reasoner.output.graph";

  /** max path limit use __max_path_size__ compatible with 1.0 */
  public static final String KG_REASONER_MAX_PATH_LIMIT = "__max_path_size__";

  /** max path limit, exceeding threshold will throw an exception only for local runner */
  public static final String KG_REASONER_STRICT_MAX_PATH_THRESHOLD =
      "kg.reasoner.strict.path.threshold";

  /** edge limit for one vertex use __max_edge_size__ compatible with 1.0 */
  public static final String KG_REASONER_MAX_EDGE_LIMIT = "__max_edge_size__";

  /** edge limit for one vertex and one type */
  public static final String KG_REASONER_MAX_EDGE_EACH_TYPE_LIMIT = "__max_edge_size_each_type__";

  /** dsl start id info */
  public static final String KG_REASONER_START_ID_INFO = "start.id.info";

  /** initializer class list */
  public static final String KG_REASONER_INITIALIZER_CLASS_LIST =
      "kg.reasoner.initializer.class.list";

  /** add extra schema to catalog */
  public static final String KG_REASONER_EXTRA_SCHEMA_JSON = "kg.reasoner.extra.schema.json";

  /** extra graph load config */
  public static final String KG_REASONER_EXTRA_GRAPH_LOAD_CONFIG =
      "kg.reasoner.extra.graph.load.config";

  /** split KgGraph to worker */
  public static final String KG_REASONER_SPLIT_TO_WORKER = "kg.reasoner.enable.split.to.worker";

  /** kgstate version condition */
  public static final String KG_STATE_VERSION = "kg.state.version";

  /** disable edge spot duplicate remove */
  public static final String KG_REASONER_DISABLE_EDGE_SPOT_DUPLICATE_REMOVE =
      "kg.reasoner.disable.edge.spot.duplicate.remove";

  /** edge extra identifier, separated by commas */
  public static final String EDGE_EXTRA_IDENTIFIER = "kg.reasoner.edge.extra.identifier";

  /** the devId of akg task */
  public static final String DEV_ID = "devId";
}
