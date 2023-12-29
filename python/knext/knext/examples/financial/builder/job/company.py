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


from knext.examples.financial.schema.financial_schema_helper import Financial

from knext.api.component import CSVReader, LLMBasedExtractor, KGWriter, SubGraphMapping
from knext.client.model.builder_job import BuilderJob
from nn4k.invoker import LLMInvoker


class Company(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="builder/job/data/company.csv", columns=["input"], start_row=2
        )

        from knext.api.auto_prompt import REPrompt

        prompt = REPrompt(
            spg_type_name=Financial.Company,
            property_names=[
                Financial.Company.name,
                Financial.Company.orgCertNo,
                Financial.Company.regArea,
                Financial.Company.businessScope,
                Financial.Company.establishDate,
                Financial.Company.legalPerson,
                Financial.Company.regCapital,
            ],
        )

        extract = LLMBasedExtractor(
            llm=LLMInvoker.from_config("builder/model/openai_infer.json"),
            prompt_ops=[prompt],
        )

        mapping = (
            SubGraphMapping(spg_type_name=Financial.Company)
            .add_mapping_field("name", Financial.Company.id)
            .add_mapping_field("name", Financial.Company.name)
            .add_mapping_field("regArea", Financial.Company.regArea)
            .add_mapping_field("businessScope", Financial.Company.businessScope)
            .add_mapping_field("establishDate", Financial.Company.establishDate)
            .add_mapping_field("legalPerson", Financial.Company.legalPerson)
            .add_mapping_field("regCapital", Financial.Company.regCapital)
        )

        sink = KGWriter()

        return source >> extract >> mapping >> sink


if __name__ == "__main__":
    from knext.api.auto_prompt import REPrompt

    prompt = REPrompt(
        spg_type_name=Financial.Company,
        property_names=[
            Financial.Company.orgCertNo,
            Financial.Company.regArea,
            Financial.Company.businessScope,
            Financial.Company.establishDate,
            Financial.Company.legalPerson,
            Financial.Company.regCapital,
        ],
    )
    print(prompt.template)
