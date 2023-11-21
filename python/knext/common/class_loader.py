# -*- coding: utf-8 -*-
# Copyright 2023 Ant Group CO., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

from typing import Dict

global_class = {}


class ClassLoader:
    def __init__(self):
        from pemja import findClass

        searchEngineConnectionInfo = findClass(
            "com.antgroup.openspg.common.model.datasource.connection.SearchEngineConnectionInfo"
        )
        global_class["SearchEngineConnectionInfo"] = searchEngineConnectionInfo

        elasticSearchEngineClientDriver = findClass(
            "com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchEngineClientDriver"
        )
        global_class["SearchEngineConnectionInfo"] = searchEngineConnectionInfo

    def loadClass(className: str):
        return global_class[className]


def SearchEngineConnectionInfo(scheme: str, params: Dict[str, str]):
    from pemja import findClass

    searchEngineConnectionInfo = findClass(
        "com.antgroup.openspg.common.model.datasource.connection.SearchEngineConnectionInfo"
    )
    search_connect_info = searchEngineConnectionInfo()
    search_connect_info.setScheme(scheme)
    search_connect_info.setParams(params)
    return search_connect_info


def SearchEngineClient(search_connect_info):
    from pemja import findClass

    elasticSearchEngineClientDriver = findClass(
        "com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchEngineClientDriver"
    )
    driver = elasticSearchEngineClientDriver()
    return driver.connect(search_connect_info)


def SearchRequest(query, idx_name, start, size):
    from pemja import findClass

    searchRequest = findClass(
        "com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest"
    )
    request = searchRequest()
    request.setIndexName(idx_name)
    request.setQuery(query)
    request.setFrom(start)
    request.setSize(size)
    return request


def MatchQuery(name: str, value):
    from pemja import findClass

    matchQuery = findClass(
        "com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.MatchQuery"
    )
    return matchQuery(name, value)


def TermQuery(term: str, value):
    from pemja import findClass

    termQuery = findClass(
        "com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.TermQuery"
    )
    return termQuery(term, value)


def QueryGroup(queries: list, op):
    from pemja import findClass

    queryGroup = findClass(
        "com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.QueryGroup"
    )
    return queryGroup(queries, op)


def OperatorType():
    from pemja import findClass

    operatorType = findClass(
        "com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.OperatorType"
    )
    return operatorType
