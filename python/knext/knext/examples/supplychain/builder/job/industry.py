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
    CSVReader,
    KGWriter,
    SPGTypeMapping,
)
from schema.supplychain_schema_helper import SupplyChain


class Industry(BuilderJob):
    parallelism = 10

    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Industry.csv",
            columns=["fullname"],
            start_row=2,
        )

        mapping = SPGTypeMapping(spg_type_name=SupplyChain.Industry).add_field(
            "fullname", SupplyChain.Industry.id
        )

        sink = KGWriter()

        return source >> mapping >> sink
