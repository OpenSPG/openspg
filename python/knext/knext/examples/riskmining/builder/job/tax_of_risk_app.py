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

from knext.builder.model.builder_job import BuilderJob
from knext.builder.component import (
    CSVReader,
    KGWriter,
    SPGTypeMapping,
)
from schema.riskmining_schema_helper import RiskMining


class TaxOfRiskApp(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/TaxOfRiskApp.csv",
            columns=["id"],
            start_row=2,
        )

        mapping = (
            SPGTypeMapping(spg_type_name=RiskMining.TaxOfRiskApp)
            .add_property_mapping("id", RiskMining.TaxOfRiskApp.id)
            .add_property_mapping("id", RiskMining.TaxOfRiskApp.name)
        )

        sink = KGWriter()

        return source >> mapping >> sink
