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

import json
import pprint
from typing import Dict

from elasticsearch import Elasticsearch

from knext import rest


class IdxRecord:
    """Represents a record retrieved from an index.

    Attributes:
        index_name (str): The name of the index where the record belongs.
        doc_id (str): The unique identifier of the document.
        score (float): The relevance score of the record.
        properties (Dict[str, str]): A dictionary containing the properties or fields of the record.

    """

    def __init__(
        self, index_name: str, doc_id: str, score: float, properties: Dict[str, str]
    ):
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


class SearchClient:
    """Client connected to search engine, which can be imported in operator to recall entities.
    You can initialize this client in `BaseOp.__init__()` and invoke `search` method in `BaseOp.eval()`.

    """

    def __init__(self, spg_type: str):
        # host_addr = f'{conn_info["params"]["scheme"]}://{conn_info["params"]["host"]}:{conn_info["params"]["port"]}'

        self.index_name = '-'.join(['$' + s for s in spg_type.split('.')])
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
                        hit.get("_index"),
                        hit.get("_id"),
                        hit.get("_score"),
                        hit.get("_source"),
                    )
                )
            return records
        return None
