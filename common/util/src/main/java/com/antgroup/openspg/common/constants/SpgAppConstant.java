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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
      new HashSet<String>(Arrays.asList("_content_vector", "_name_vector", "_desc_vector"));

  /** text/event-stream */
  public static final String STEAM_CONTENT_TYPE = "text/event-stream;charset=UTF-8";

  /** json/application */
  public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

  /** stream end line */
  public static final String STREAM_END_LINE = "data: [DONE]\n\n";

  /** stream timeout line */
  public static final String STREAM_TIMEOUT_LINE = "data: [TIMEOUT]\n\n";

  /** stream error line */
  public static final String STREAM_ERROR_LINE = "data: [ERROR]\n\n";
}
