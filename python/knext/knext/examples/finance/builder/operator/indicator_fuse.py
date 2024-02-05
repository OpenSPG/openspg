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
import requests
from knext.builder.operator import FuseOp
from knext.builder.operator.spg_record import SPGRecord
from knext.common.search import SearchClient

from schema.finance_schema_helper import Finance


class IndicatorFuseOp(FuseOp):
    bind_to = Finance.Indicator

    def __init__(self):
        super().__init__()
        from builder.operator.prompts import IndicatorFusePrompt

        self.prompt_op = IndicatorFusePrompt()
        self.search_client = SearchClient(Finance.Indicator)

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

    def link(self, subject_record: SPGRecord) -> SPGRecord:
        # Retrieve relevant indicators from KG based on indicator name
        recall_records = self.search_client.fuzzy_search(subject_record, "name", size=1)
        if len(recall_records) == 0:
            return subject_record
        return recall_records[0]

    def merge(self, subject_record: SPGRecord, linked_record: SPGRecord) -> SPGRecord:
        # Merge the recalled indicators with LLM
        data = {
            "name": subject_record.get_property("name"),
            "candidates": [linked_record.properties["name"]],
        }
        merge_input = self.prompt_op.build_prompt(data)
        merge_result = self.generate(merge_input)
        merge_result = self.prompt_op.parse_response(merge_result)
        # If the KG already contains `subject_record`, return the existing record
        # (you can also update the properties of existing record as well),
        # otherwise return `subject_record`

        if merge_result is not None:
            tmp = merge_result[0]
            is_a_relation = subject_record.get_relation("isA", Finance.Indicator)
            if is_a_relation is not None:
                tmp.upsert_relation("isA", Finance.Indicator, is_a_relation)
            return tmp
        else:
            return subject_record
