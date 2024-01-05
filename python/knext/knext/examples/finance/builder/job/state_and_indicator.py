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


from schema.finance_schema_helper import Finance

from knext.api.component import CSVReader, LLMBasedExtractor, KGWriter, SPGTypeMapping
from knext.client.model.builder_job import BuilderJob
from nn4k.invoker import LLMInvoker


class StateAndIndicator(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="builder/job/data/document.csv", columns=["input"], start_row=2
        )

        from builder.operator.prompt.indicator_extraction import IndicatorNER
        from builder.operator.prompt.relation_extraction import IndicatorREL
        from builder.operator.prompt.logic_relation_extraction import (
            IndicatorLogic,
        )

        extract = LLMBasedExtractor(
            llm=LLMInvoker.from_config("builder/model/openai_infer.json"),
            prompt_ops=[IndicatorNER(), IndicatorREL(), IndicatorLogic()],
        )

        state_mapping = (
            SPGTypeMapping(spg_type_name=Finance.State)
            .add_property_mapping("id", Finance.State.id)
            .add_property_mapping("name", Finance.State.name)
            .add_relation_mapping("causes", Finance.State.causes, Finance.State)
            .add_predicting_relation(Finance.State.derivedFrom, Finance.Indicator)
        )

        indicator_mapping = (
            SPGTypeMapping(spg_type_name=Finance.Indicator)
        )

        sink = KGWriter()

        return source >> extract >> [state_mapping, indicator_mapping] >> sink
