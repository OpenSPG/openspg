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

from knext.client.model.builder_job import BuilderJob
from knext.api.component import (
    CsvSourceReader,
    KGSinkWriter,
    SPGTypeMapping,
)
from schema.riskmining_schema_helper import RiskMining


class App(BuilderJob):
    def build(self):
        source = CsvSourceReader(
            local_path="./builder/job/data/App.csv",
            columns=["id", "riskMark", "useCert"],
            start_row=2,
        )

        mapping = (
            SPGTypeMapping(spg_type_name=RiskMining.App)
            .add_field("id", RiskMining.App.id)
            .add_field("id", RiskMining.App.name)
            .add_field("riskMark", RiskMining.App.riskMark)
            .add_field("useCert", RiskMining.App.useCert)
        )

        sink = KGSinkWriter()

        return source >> mapping >> sink
