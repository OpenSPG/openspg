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
    KGWriter
)
from knext.operator.base import BaseOp


class Disease(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Disease.csv",
            columns=["id", "content"],
            start_row=2,
        )


        # from operator.disease_extractor import DiseaseExtractor
        extract = UserDefinedExtractor(output_fields=["id", "name"], extract_op=BaseOp.by_name('DiseaseExtractor')({"config": "1"}))

        mapping = SPGTypeMapping(spg_type_name="Medical.Disease").add_field("id", "id").add_field("name", "name")

        """
        3. 定义输出到图谱
        """
        sink = KGWriter()

        return source >> extract >> mapping >> sink
