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

package com.antgroup.openspg.cloudext.interfaces.searchengine.constants;

public class ElasticSearchConstants {

  /** The must clause keyword of ElasticSearch. */
  public static final String ES_QUERY_MUST_CLAUSE_KEY = "must";

  /** The not clause keyword of ElasticSearch. */
  public static final String ES_QUERY_NOT_CLAUSE_KEY = "must_not";

  /** The should clause keyword of ElasticSearch. */
  public static final String ES_QUERY_SHOULD_CLAUSE_KEY = "should";

  /** The term clause keyword of ElasticSearch. */
  public static final String ES_QUERY_TERM_CLAUSE_KEY = "term";

  /** The match clause keyword of ElasticSearch. */
  public static final String ES_QUERY_MATCH_CLAUSE_KEY = "match";

  /** The bool clause keyword of ElasticSearch. */
  public static final String ES_QUERY_BOOL_CLAUSE_KEY = "bool";

  /** The hits keyword of ElasticSearch query results */
  public static final String ES_RESULT_HITS_KEY = "hits";
}
