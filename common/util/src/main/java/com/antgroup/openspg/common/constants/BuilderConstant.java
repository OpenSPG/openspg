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
package com.antgroup.openspg.common.constants;

public class BuilderConstant {

  public static final String DOT = ".";

  public static final String PROJECT_ID = "project_id";
  public static final String TOKEN = "token";
  public static final String MAPPING_CONFIG = "mappingConfig";
  public static final String YU_QUE_CONFIG = "yuqueConfig";
  public static final String YU_QUE_URL = "yuQueUrl";
  public static final String YU_QUE_TOKEN = "yuQueToken";
  public static final String SPLIT_CONFIG = "splitConfig";
  public static final String EXTRACT_CONFIG = "extractConfig";
  public static final String SEMANTIC_SPLIT = "semanticSplit";
  public static final String AUTO_WRITE = "autoWrite";
  public static final String AUTO_SCHEMA = "autoSchema";
  public static final String SPLIT_LENGTH = "splitLength";
  public static final String PY_SPLIT_LENGTH = "split_length";
  public static final String PY_WINDOW_LENGTH = "window_length";
  public static final String DEFAULT_VERSION = "V3";
  public static final String KAG_COMMAND = "KAG_COMMAND";
  public static final String KAG_WRITER_ASYNC_TASK = "kagWriterAsyncTask";
  public static final String SYSTEM = "system";

  public static final String DATASOURCE_CONFIG = "dataSourceConfig";
  public static final String DATASOURCE = "dataSource";
  public static final String COLUMNS = "columns";
  public static final String INDEX = "index";
  public static final String NAME = "name";
  public static final String IMPORT_CONFIG = "importConfig";
  public static final String JOB_LIFECYCLE = "jobLifeCycle";
  public static final String SCHEDULE_START_DATE = "scheduleStartDate";
  public static final String MERGE_MODE = "mergeMode";
  public static final String JOB_ACTION = "jobAction";
  public static final String ADVANCED_CONFIG = "advancedConfig";
  public static final String COMPUTING_TYPE = "computingType";
  public static final String BUILDER_TYPE = "builderType";
  public static final String KAG = "kag";
  public static final String LOCAL = "local";
  public static final String AISTUDIO = "aistudio";
  public static final String DISTRIBUTED = "distributed";
  public static final String COMMAND = "command";
  public static final String SPG_DEFAULT_COMMAND =
      "pip install openspg-kag-ant==0.7.0.202505062 -i https://artifacts.antgroup-inc.cn/artifact/repositories/simple-dev/ "
          + "&&pip uninstall -y openspg-kag "
          + "&&pip install openspg-kag==0.7.1.20250605 -i https://artifacts.antgroup-inc.cn/artifact/repositories/simple-dev/ "
          + "&&python -c 'import kag_ant; from kag.bridge.spg_server_bridge import init_kag_config; init_kag_config(\"%s\",\"%s\"); from kag.bridge.spg_server_bridge import SPGServerBridge; builder = SPGServerBridge(); builder.run_builder(%s,\"%s\")'";
  public static final String STRUCTURE = "structure";
  public static final String STRUCTURED_BUILDER_CHAIN = "structured_builder_chain";
  public static final String UNSTRUCTURED_BUILDER_CHAIN = "unstructured_builder_chain";

  public static final String SCANNER = "scanner";
  public static final String READER = "reader";
  public static final String TYPE = "type";
  public static final String ID_COL = "id_col";
  public static final String NAME_COL = "name_col";
  public static final String CONTENT_COL = "content_col";
  public static final String HEADER = "header";
  public static final String COL_NAMES = "col_names";
  public static final String COL_MAP = "col_map";
  public static final String COL_IDS = "col_ids";
  public static final String CUT_DEPTH = "cut_depth";
  public static final String LLM = "llm";
  public static final String VECTORIZE_MODEL = "vectorize_model";
  public static final String ID = "id";
  public static final String CONTENT = "content";
  public static final String DICT = "dict";
  public static final String FILE = "file";
  public static final String SEMANTIC = "semantic";
  public static final String LENGTH = "length";
  public static final String BATCH = "batch";
  public static final String BASE = "base";
  public static final String SCHEMA_FREE = "schema_free";
  public static final String SCHEMA_CONSTRAINT_EXTRACTOR = "schema_constraint_extractor";
  public static final String SPLITTER_ABC = "SplitterABC";
  public static final String EXTRACTOR_ABC = "ExtractorABC";
  public static final String VECTORIZER_ABC = "VectorizerABC";
  public static final String POSTPROCESSOR_ABC = "PostProcessorABC";
  public static final String MAPPING_ABC = "MappingABC";

  public static final String SPG_MAPPING = "spg_mapping";
  public static final String RELATION = "relation";
  public static final String SPG_TYPE_NAME = "spg_type_name";
  public static final String PROPERTY_MAPPING = "property_mapping";
  public static final String SUBJECT_NAME = "subject_name";
  public static final String PREDICATE_NAME = "predicate_name";
  public static final String OBJECT_NAME = "object_name";
  public static final String SRC_ID_FIELD = "src_id_field";
  public static final String DST_ID_FIELD = "dst_id_field";

  public static final String MAPPING_TYPE = "mappingType";
  public static final String ENTITY_MAPPING = "entityMapping";
  public static final String RELATION_MAPPING = "relationMapping";
  public static final String FILTER = "filter";
  public static final String CONFIG = "config";
  public static final String S = "s";
  public static final String P = "p";
  public static final String O = "o";
  public static final String MAPPING = "mapping";
  public static final String END_ID = "end_id";
  public static final String START_ID = "start_id";
  public static final String SRC_ID = "_src_id";
  public static final String DST_ID = "_dst_id";

  public static final String YU_QUE = "yuque";
  public static final String TXT = "txt";
  public static final String CSV = "csv";
  public static final String PDF = "pdf";
  public static final String MD = "md";
  public static final String JSON = "json";
  public static final String DOC = "doc";
  public static final String DOCX = "docx";
  public static final String ODPS = "odps";
  public static final String SLS = "sls";

  public static final String CSV_STRUCTURED = "csv_structured";
  public static final String IGNORE_HEADER = "ignoreHeader";

  public static final String ODPS_SCANNER = "odps_scanner";
  public static final String ACCESS_ID = "access_id";
  public static final String ACCESS_KEY = "access_key";
  public static final String PROJECT = "project";
  public static final String TABLE = "table";
  public static final String ENDPOINT = "endpoint";
  public static final String PARTITION = "partition";
  public static final String DATABASE = "database";

  public static final String SLS_CONSUMER_SCANNER = "sls_consumer_scanner";
  public static final String SLS_ACCESS_ID = "accessId";
  public static final String SLS_ACCESS_KEY = "accessKey";
  public static final String SLS_END_POINT = "endPoint";
  public static final String LOG_STORE = "logstore";
  public static final String SLS_LOG_STORE = "logStore";

  public static final String SCHEDULER_CRON = "0 0 0 * * ?";
  public static final String CONTAIN_ALL = "*";
  public static final String DOLLAR_SEPARATOR = "$";

  public static final String BIZ_SCENE = "biz_scene";
  public static final String DEFAULT = "default";
  public static final String HOST_ADDR = "host_addr";
  public static final String LANGUAGE = "language";
  public static final String NAMESPACE = "namespace";
  public static final String KAG_BUILDER_PIPELINE = "kag_builder_pipeline";
  public static final String CHAIN = "chain";
  public static final String NUM_THREADS_PER_CHAIN = "num_threads_per_chain";
  public static final String NUM_CHAINS = "num_chains";

  public static final String EXTRACTOR = "extractor";
  public static final String POST_PROCESSOR = "post_processor";
  public static final String SPLITTER = "splitter";
  public static final String VECTORIZER = "vectorizer";
  public static final String WRITER = "writer";
  public static final String KG_WRITER = "kg_writer";
  public static final String DELETE = "delete";
  public static final String BATCH_VECTORIZER = "batch_vectorizer";
  public static final String KAG_POST_PROCESSOR = "kag_post_processor";

  public static final String TOKENS_COST = "tokensCost";
  public static final String TIME_COST = "timeCost";
  public static final String STORAGE_COST = "storageCost";
  public static final String COMPLETION_TOKENS = "completion_tokens";
}
