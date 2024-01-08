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
    UserDefinedExtractor,
    SPGTypeMapping,
    KGWriter,
)

try:
    from schema.test_schema_helper import TEST
except:
    pass


class Pre(BuilderJob):

    lead_to = True

    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/pre.csv",
            columns=["id", "filter"],
            start_row=2,
        )

        entity_mapping_2 = (
            SPGTypeMapping(spg_type_name=TEST.Entity2)
            .add_mapping_field("id", TEST.Entity2.id)
            .add_mapping_field("id", TEST.Entity2.name)
            .add_filter("filter", "2")
        )

        entity_mapping_3 = (
            SPGTypeMapping(spg_type_name=TEST.Entity3)
            .add_mapping_field("id", TEST.Entity3.id)
            .add_mapping_field("id", TEST.Entity3.name)
            .add_filter("filter", "3")
        )

        sink = KGWriter()

        return source >> [entity_mapping_2, entity_mapping_3] >> sink
