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
from typing import List, Dict
from knext.api.operator import ExtractOp
from knext.api.record import SPGRecord


class IndicatorExtractOp(ExtractOp):
    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)
        # Address for LLM service
        self.url = self.params["url"]
        from builder.operator.prompts import IndicatorNERPrompt
        self.prompt_op = IndicatorNERPrompt()

    def generate(self, input_data, adapter_name):
        # Request LLM service to get the extraction results
        req = {
            "input": input_data,
            "adapter_name": adapter_name,
            "max_input_len": 1024,
            "max_output_len": 1024,
        }
        try:
            rsp = requests.post(self.url, req)
            rsp.raise_for_status()
            return rsp.json()
        except Exception as e:
            return {"output": ""}

    def invoke(self, record: Dict[str, str]) -> List[SPGRecord]:
        # Building LLM inputs with IndicatorNERPrompt
        ner_input = self.prompt_op.build_prompt(record)
        ner_output = self.generate(ner_input, "ner")
        record["ner"] = ner_output["output"]
        # Parsing the LLM output with IndicatorNERPrompt to construct SPGRecords
        ner_result = self.prompt_op.parse_response(record["ner"])

        return ner_result
