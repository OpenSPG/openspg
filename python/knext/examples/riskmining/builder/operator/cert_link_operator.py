# -*- coding: utf-8 -*-
#
#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

from typing import List

from knext.core.builder.operator import Vertex
from knext.core.builder.operator.model.op import EntityLinkOp
from knext.core.wrapper.search_client import SearchClient


class CertLinkerOperator(EntityLinkOp):
    bind_to = "RiskMining.Cert"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("RiskMining.Cert")

    def eval(self, property: str, record: Vertex) -> List[Vertex]:
        has_cert = property
        query = {"match": {"certNum": has_cert}}
        recall_certs = self.search_client.search(query, start=0, size=10)
        if recall_certs is not None:
            return [Vertex(biz_id=recall_certs[0].doc_id, vertex_type="RiskMining.Cert")]
        return [Vertex(biz_id=has_cert, vertex_type="RiskMining.Cert")]
