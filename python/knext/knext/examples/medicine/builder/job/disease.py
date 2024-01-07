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

from nn4k.invoker import NNInvoker

from knext.api.component import CSVReader, LLMBasedExtractor, SPGTypeMapping, KGWriter
from knext.api.auto_prompt import REPrompt
from knext.client.model.builder_job import BuilderJob


from schema.medicine_schema_helper import Medicine


class Disease(BuilderJob):
    def build(self):

        source = CSVReader(
            local_path="builder/job/data/Disease.csv",
            columns=["input"],
            start_row=1,
        )

        extract = LLMBasedExtractor(
            llm=NNInvoker.from_config("builder/model/openai_infer.json"),
            prompt_ops=[
                REPrompt(
                    spg_type_name=Medicine.Disease,
                    property_names=[
                        Medicine.Disease.complication,
                        Medicine.Disease.commonSymptom,
                        Medicine.Disease.applicableDrug,
                        Medicine.Disease.department,
                        Medicine.Disease.diseaseSite,
                    ],
                    relation_names=[(Medicine.Disease.abnormal, Medicine.Indicator)],
                )
            ],
        )

        mappings = [
            SPGTypeMapping(spg_type_name=Medicine.Disease),
            SPGTypeMapping(spg_type_name=Medicine.BodyPart),
            SPGTypeMapping(spg_type_name=Medicine.Drug),
            SPGTypeMapping(spg_type_name=Medicine.HospitalDepartment),
            SPGTypeMapping(spg_type_name=Medicine.Symptom),
            SPGTypeMapping(spg_type_name=Medicine.Indicator),
        ]

        sink = KGWriter()

        return source >> extract >> mappings >> sink
