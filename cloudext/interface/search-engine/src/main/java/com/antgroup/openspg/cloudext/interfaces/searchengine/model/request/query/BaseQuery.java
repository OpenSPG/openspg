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

package com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query;

/**
 * This is the base class for Queries, aimed at providing an intermediate layer for users to
 * assemble query requests and achieve independence from the search engine. During the retrieval
 * process, the server will convert the Query into corresponding search engine-specific query
 * conditions, such as ES and Ha3.
 *
 * <p>Taking Elasticsearch and Ha3 search engines as examples, the following are examples of the
 * converted query strings:
 *
 * <ul>
 *   <li>Elasticsearch - GET spg_data/_search { "query": { "match": { "name": "alipay" } }, "from":
 *       1, "size": 10 }
 *   <li>Ha3 Search Engine - query=name:'alipay' &&config=cluster:spg_data,hit:10,start:0
 * </ul>
 */
public abstract class BaseQuery {}
