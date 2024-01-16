# -*- coding: utf-8 -*-
# Copyright 2023 OpenSPG Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

import pprint
from typing import Dict, List, Optional

from elasticsearch import Elasticsearch

from knext import rest
from knext.common.schema_helper import PropertyName
from knext.operator.spg_record import SPGRecord


class IdxRecord:
    """Represents a record retrieved from an index.

    Attributes:
        index_name (str): The name of the index where the record belongs.
        doc_id (str): The unique identifier of the document.
        score (float): The relevance score of the record.
        properties (Dict[str, str]): A dictionary containing the properties or fields of the record.

    """

    def __init__(
        self,
        spg_type_name: str,
        index_name: str,
        doc_id: str,
        score: float,
        properties: Dict[str, str],
    ):
        self.spg_type_name = spg_type_name
        self.index_name = index_name
        self.doc_id = doc_id
        self.score = score
        self.properties = properties

    def __repr__(self):
        """For `print` and `pprint`"""
        return self.to_str()

    def to_str(self):
        """Returns the string representation of the model"""
        return pprint.pformat(self.__dict__)

    def to_spg_record(self):
        record = SPGRecord(self.spg_type_name)
        record.upsert_property("id", self.doc_id)
        for name, value in self.properties.items():
            record.upsert_property(name, value)
        return record


class SearchClient:
    """Client connected to search engine, which can be imported in operator to recall entities.
    You can initialize this client in `BaseOp.__init__()` and invoke `search` method in `BaseOp.eval()`.

    """

    def __init__(self, spg_type_name: str):
        _client = rest.BuilderApi()
        response = _client.search_engine_index_get(spg_type=spg_type_name)

        self.index_name = response.index_name
        self.spg_type_name = spg_type_name
        self.client = Elasticsearch("http://127.0.0.1:9200")

    def search(self, query, sort=None, filter=None, start: int = 0, size: int = 10):
        """Perform a search operation on the specified index using the given query.

        Args:
            query: The query to be executed for the search.
            sort: Optional. The sorting criteria for the search results.
            filter: Optional. The filter to be applied to the search results.
            start: Optional. The starting position of the search results to be returned. Default is 0.
            size: Optional. The maximum number of search results to be returned. Default is 10.

        Returns:
            A list of `IdxRecord` objects representing the search results, or None if no results were found.

        """
        data = self.client.search(
            index=self.index_name,
            query=query,
            sort=sort,
            post_filter=filter,
            from_=start,
            size=size,
        )
        if "hits" in data and "hits" in data.get("hits"):
            hits = data.get("hits").get("hits")
            records = []
            for hit in hits:
                records.append(
                    IdxRecord(
                        self.spg_type_name,
                        hit.get("_index"),
                        hit.get("_id"),
                        hit.get("_score"),
                        hit.get("_source"),
                    )
                )
            return records
        return None

    def fuzzy_search(
        self, record: SPGRecord, property_name: PropertyName, size: int = 10
    ) -> List[SPGRecord]:
        property_value = record.get_property(property_name)
        if not property_value:
            return []
        query = {"match": {property_name: property_value}}
        records = []
        recall_results = self.search(query, size=size)
        if recall_results:
            for recall_result in recall_results:
                records.append(recall_result.to_spg_record())
        return records

    def exact_search(
        self, record: SPGRecord, property_name: PropertyName
    ) -> Optional[SPGRecord]:
        property_value = record.get_property(property_name)
        if not property_value:
            return None
        query = {"match": {property_name: property_value}}
        recall_results = self.search(query, size=1)
        if recall_results:
            if recall_results[0].properties.get(property_name) == property_value:
                return recall_results[0].to_spg_record()
        return None

    def exact_search_by_property(
        self, property_value: str, property_name: PropertyName
    ) -> Optional[SPGRecord]:
        query = {"match": {property_name: property_value}}
        recall_results = self.search(query, size=1)
        if recall_results:
            if recall_results[0].properties.get(property_name) == property_value:
                return recall_results[0].to_spg_record()
        return None

    def fuzzy_search_by_property(
        self, property_value: str, property_name: PropertyName, size: int = 10
    ) -> List[SPGRecord]:
        query = {"match": {property_name: property_value}}
        records = []
        recall_results = self.search(query, size=size)
        if recall_results:
            for recall_result in recall_results:
                records.append(recall_result.to_spg_record())
        return records
