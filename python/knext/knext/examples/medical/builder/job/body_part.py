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
from knext.component.builder import CSVReader, SPGTypeMapping, KGWriter


class BodyPart(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/BodyPart.csv", columns=["id"], start_row=1
        )

        mapping = SPGTypeMapping(spg_type_name="Medical.BodyPart").add_field(
            "id", "id"
        )

        sink = KGWriter()

        return source >> mapping >> sink
