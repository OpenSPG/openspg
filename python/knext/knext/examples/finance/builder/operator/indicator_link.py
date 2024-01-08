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
import requests
from typing import List
from knext.api.operator import LinkOp
from knext.api.record import SPGRecord
from knext.api.client import SearchClient

from schema.finance_schema_helper import Finance

class IndicatorLinkOp(LinkOp):
    bind_to = Finance.Indicator

    def __init__(self):
        super().__init__()
        from builder.operator.prompts import IndicatorLinkPrompt
        self.prompt_op = IndicatorLinkPrompt()
        self.search_client = SearchClient(self.bind_to)

    def generate(self, input_data):
        req = {
            "input": input_data,
            "max_input_len": 1024,
            "max_output_len": 1024,
        }
        url = "http://localhost:9999/generate"
        try:
            rsp = requests.post(url, req)
            rsp.raise_for_status()
            return rsp.json()
        except Exception as e:
            return {"output": ""}

    def invoke(self, property: str, subject_record: SPGRecord) -> List[SPGRecord]:
        # Retrieve relevant indicators from KG based on indicator name
        name = property
        recall_records = self.search_client.fuzzy_search_by_property(property, name)
        # Reranking the realled records with LLM to get final linking result
        data = {
            "input": name,
            "candidates": [x.properties["name"] for x in recall_records],
        }
        link_input = self.prompt_op.build_prompt(data)
        link_result = self.generate(link_input)
        return self.prompt_op.parse_response(link_result)
