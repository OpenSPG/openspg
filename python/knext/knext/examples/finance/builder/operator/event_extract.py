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
from nn4k.invoker import NNInvoker


class EventExtractOp(ExtractOp):
    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)
        # Address for LLM service
        self.config = params["config"]
        self.invoker = NNInvoker.from_config(self.config)
        from builder.operator.prompts import EventExtractPrompt

        self.prompt_op = EventExtractPrompt()

    def generate(self, input_data):
        return self.invoker.remote_inference(input_data)[0]

    def invoke(self, record: Dict[str, str]) -> List[SPGRecord]:
        # Building LLM inputs with IndicatorNERPrompt
        input_data = self.prompt_op.build_prompt(record)
        output = self.generate(input_data)
        records = self.prompt_op.parse_response(output)
        return records
