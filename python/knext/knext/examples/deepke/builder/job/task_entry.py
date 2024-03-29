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

from nn4k.invoker import NNInvoker
from knext.builder.component import (
    CSVReader,
    LLMBasedExtractor,
    SPGTypeMapping,
    KGWriter,
)
from knext.builder.operator import OneKE_KGPrompt
from knext.builder.model.builder_job import BuilderJob

from schema.deepke_schema_helper import DeepKE


class Disease(BuilderJob):
    def build(self):

        source = CSVReader(
            local_path="builder/job/data/Disease.csv",
            columns=["input"],
            start_row=1,
        )

        extract = LLMBasedExtractor(
            llm=NNInvoker.from_config("builder/model/remote_infer.json"),
            prompt_ops=[
                OneKE_KGPrompt(
                    entity_types=[
                        DeepKE.Disease,
                        DeepKE.BodyPart,
                        DeepKE.Drug,
                        DeepKE.HospitalDepartment,
                        DeepKE.Symptom,
                        DeepKE.Indicator,
                    ]
                )
            ],
        )

        mappings = [
            SPGTypeMapping(spg_type_name=DeepKE.Disease),
            SPGTypeMapping(spg_type_name=DeepKE.BodyPart),
            SPGTypeMapping(spg_type_name=DeepKE.Drug),
            SPGTypeMapping(spg_type_name=DeepKE.HospitalDepartment),
            SPGTypeMapping(spg_type_name=DeepKE.Symptom),
            SPGTypeMapping(spg_type_name=DeepKE.Indicator),
        ]

        sink = KGWriter()

        return source >> extract >> mappings >> sink
