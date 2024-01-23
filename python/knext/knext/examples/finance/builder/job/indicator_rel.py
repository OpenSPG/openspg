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


from schema.finance_schema_helper import Finance

from knext.builder.component import (
    CSVReader,
    UserDefinedExtractor,
    KGWriter,
    SPGTypeMapping,
)
from knext.builder.model.builder_job import BuilderJob
from nn4k.invoker import NNInvoker

from builder.operator.indicator_extract import IndicatorExtractOp


class IndicatorRel(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="builder/job/data/document.csv", columns=["input"], start_row=2
        )

        extract = UserDefinedExtractor(
            extract_op=IndicatorExtractOp(
                params={"url": "http://localhost:9999/generate"}
            ),
        )

        indicator_mapping = (
            SPGTypeMapping(spg_type_name=Finance.Indicator)
            .add_property_mapping("id", Finance.Indicator.id)
            .add_property_mapping("name", Finance.Indicator.name)
            .add_predicting_relation("isA", Finance.Indicator)
        )

        sink = KGWriter()

        return source >> extract >> [indicator_mapping] >> sink
