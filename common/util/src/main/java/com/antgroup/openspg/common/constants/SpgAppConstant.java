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

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SpgAppConstant {

  /** default vectorizer api key */
  public static final String DEFAULT_VECTORIZER_API_KEY = "******";

  /** graph store */
  public static final String GRAPH_STORE = "graph_store";

  /** vectorizer */
  public static final String VECTORIZER = "vectorizer";

  /** llm_select */
  public static final String LLM_SELECT = "llm_select";

  /** llm_select */
  public static final String LLM_ID = "llm_id";

  /** llm */
  public static final String LLM = "llm";

  /** chat_llm */
  public static final String CHAT_LLM = "chat_llm";

  /** openie_llm */
  public static final String OPENIE_LLM = "openie_llm";

  /** default */
  public static final String DEFAULT = "default";

  /** api key */
  public static final String API_KEY = "api_key";

  /** api key */
  public static final String KEY = "key";

  /** password */
  public static final String PASSWORD = "password";

  /** kag config */
  public static final String KAG_CONFIG = "KAG_CONFIG";

  /** kag env */
  public static final String KAG_ENV = "KAG_ENV";

  /** kag config */
  public static final String APPLICATION_PROPERTIES = "APPLICATION_PROPERTIES";

  /** client type */
  public static final String CLIENT_TYPE = "client_type";

  /** type */
  public static final String TYPE = "type";

  /** model */
  public static final String MODEL = "model";

  /** openai */
  public static final String OPENAI = "openai";

  /** default page */
  public static final Integer DEFAULT_PAGE = 1;

  /** default pageSize */
  public static final Integer DEFAULT_PAGE_SIZE = 10;

  /** line separator */
  public static final String LINE_SEPARATOR = "\n";

  /** empty line */
  public static final String EMPTY_LINE = "\n\n";

  /** tab separator */
  public static final String TAB_SEPARATOR = "\t";

  /** success */
  public static final String SUCCESS = "success";

  /** IAM_TOKEN */
  public static final String IAM_TOKEN = "IAM_TOKEN";

  /** USER_NOT_LOGIN */
  public static final String USER_NOT_LOGIN = "USER_NOT_LOGIN";

  /** OPEN_SPG_TOKEN */
  public static final String OPEN_SPG_TOKEN = "OPEN_SPG_TOKEN";

  /** open_spg_token_secret */
  public static final String OPEN_SPG_TOKEN_SECRET = "open_spg_token_secret";

  /** colon */
  public static final String COLON_SEPARATOR = ":";

  /** useCurrentLanguage */
  public static final String USE_CURRENT_LANGUAGE = "useCurrentLanguage";

  /** currentLanguage */
  public static final String CURRENT_LANGUAGE = "currentLanguage";

  /** LANGUAGE */
  public static final String LANGUAGE = "language";

  /** temperature */
  public static final String TEMPERATURE = "temperature";

  /** stream */
  public static final String STREAM = "stream";

  /** base_url */
  public static final String BASE_URL = "base_url";

  /** vector_dimensions */
  public static final String VECTOR_DIMENSIONS = "vector_dimensions";

  /** creator */
  public static final String CREATOR = "creator";

  /** createTime */
  public static final String CREATE_TIME = "createTime";

  /** desc */
  public static final String DESC = "desc";

  /** hidden property */
  public static Set<String> HIDDEN_PROPERTY =
      new HashSet<String>(
          Arrays.asList("_content_vector", "_name_vector", "_desc_vector", "_description_vector"));

  /** model id */
  public static final String MODEL_ID = "modelId";

  /** model type */
  public static final String MODEL_TYPE = "modelType";

  /** logo */
  public static final String LOGO = "logo";

  /** text/event-stream */
  public static final String STEAM_CONTENT_TYPE = "text/event-stream;charset=UTF-8";

  /** json/application */
  public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

  /** stream empty line */
  public static final String STREAM_EMPTY_LINE = "data: \n\n";

  /** stream end line */
  public static final String STREAM_END_LINE = "data: [DONE]\n\n";

  /** stream timeout line */
  public static final String STREAM_TIMEOUT_LINE = "data: [TIMEOUT]\n\n";

  /** stream error line */
  public static final String STREAM_ERROR_LINE = "data: [ERROR]\n\n";

  /** stream think end tag */
  public static final String STREAM_THINK_END_TAG = "</think>";

  /** visibility */
  public static final String VISIBILITY = "visibility";

  /** customize */
  public static final String CUSTOMIZE = "customize";

  /** mcp servers */
  public static final String MCP_SERVERS = "mcp_servers";

  /** senior */
  public static final String SENIOR = "senior";

  /** provider */
  public static final String PROVIDER = "provider";

  /** app_id */
  public static final String APP_ID = "app_id";

  /** web display inherited properties */
  public static final List<String> DISPLAY_INHERIT_PROPERTIES =
      Lists.newArrayList("name", "description");

  public static final String NAME = "name";
  public static final String NAME_ZH = "名称";

  public static final String DESCRIPTION = "description";
  public static final String DESCRIPTION_ZH = "描述";

  public static final String CHAT = "chat";
  public static final String ENAME = "ename";
  public static final String HOST_ADDR = "host_addr";
  public static final String MAYA_HTTP = "maya_http";

  /** account name check pattern */
  public static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{6,20}$");
}
