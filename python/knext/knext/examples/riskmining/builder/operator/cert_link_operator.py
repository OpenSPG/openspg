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

from typing import List

from knext.builder.operator.op import LinkOp
from knext.common.search import SearchClient
from knext.builder.operator.spg_record import SPGRecord


class CertLinkerOperator(LinkOp):
    bind_to = "RiskMining.Cert"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("RiskMining.Cert")

    def invoke(self, property: str, record: SPGRecord) -> List[SPGRecord]:
        has_cert = property

        query = {"match": {"certNum": has_cert}}
        recall_certs = self.search_client.search(query, start=0, size=10)
        if recall_certs is not None and len(recall_certs) > 0:
            return [
                SPGRecord("RiskMining.Cert").upsert_properties(
                    {"id": recall_certs[0].doc_id}
                )
            ]

        return [SPGRecord("RiskMining.Cert").upsert_properties({"id": property})]
