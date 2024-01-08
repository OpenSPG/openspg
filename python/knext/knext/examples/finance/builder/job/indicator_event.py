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

from knext.api.component import (
    CSVReader,
    UserDefinedExtractor,
    KGWriter,
    SPGTypeMapping,
)
from knext.client.model.builder_job import BuilderJob
from nn4k.invoker import NNInvoker

from builder.operator.indicator_extract import IndicatorExtractOp
from builder.operator.event_extract import EventExtractOp


class IndicatorEvent(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="builder/job/data/document.csv", columns=["input"], start_row=2
        )

        extract = UserDefinedExtractor(
            extract_op=EventExtractOp(
                params={"config": "builder/job/openai_config.json"}
            ),
        )

        event_mapping = (
            SPGTypeMapping(spg_type_name=Finance.IndicatorEvent)
            .add_property_mapping("id", Finance.IndicatorEvent.id)
            .add_property_mapping("id", Finance.IndicatorEvent.name)
            .add_property_mapping("indicator", Finance.IndicatorEvent.indicator)
            .add_property_mapping("value", Finance.IndicatorEvent.value)
            .add_property_mapping("trend", Finance.IndicatorEvent.trend)
            .add_property_mapping("date", Finance.IndicatorEvent.date)
        )

        sink = KGWriter()

        return source >> extract >> [event_mapping] >> sink
