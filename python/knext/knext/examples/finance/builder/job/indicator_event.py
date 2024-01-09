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
import os

from nn4k.invoker import NNInvoker
from schema.finance_schema_helper import Finance

from knext.api.component import (
    CSVReader,
    LLMBasedExtractor,
    KGWriter,
    SPGTypeMapping,
)
from knext.api.auto_prompt import EEPrompt
from knext.client.model.builder_job import BuilderJob


class IndicatorEvent(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="builder/job/data/document.csv", columns=["input"], start_row=2
        )

        prompt = EEPrompt(
            event_type_name=Finance.IndicatorEvent,
            property_names=[
                Finance.IndicatorEvent.subject,
                Finance.IndicatorEvent.value,
                Finance.IndicatorEvent.trend,
                Finance.IndicatorEvent.date,
            ],
        )

        extract = LLMBasedExtractor(
            llm=NNInvoker.from_config("builder/model/openai_infer.json"),
            prompt_ops=[prompt],
            debug=True
        )

        mapping = SPGTypeMapping(spg_type_name=Finance.IndicatorEvent)

        sink = KGWriter()

        return source >> extract >> mapping >> sink
