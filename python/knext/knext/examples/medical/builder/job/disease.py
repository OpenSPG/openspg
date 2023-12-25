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
    LLMBasedExtractor,
    SubGraphMapping,
    KGWriter
)
from knext.api.operator import REPrompt
from nn4k.invoker import LLMInvoker


class Disease(BuilderJob):
    def build(self):
        """
        1. 定义输入源，CSV文件
        """
        source = CSVReader(
            local_path="builder/job/data/Disease.csv",
            columns=["input"],
            start_row=1,
        )

        """
        2. 定义大模型抽取组件，从长文本中抽取Medical.Disease类型实体
        """
        extract = LLMBasedExtractor(
            llm=LLMInvoker.from_config("builder/model/openai_infer.json"),
            prompt_ops=[REPrompt(
                spg_type_name="Medical.Disease",
                property_names=[
                    "complication",
                    "commonSymptom",
                    "applicableDrug",
                    "department",
                    "diseaseSite",
                ])]
        )

        """
        2. 定义子图映射组件
        """
        mapping = SubGraphMapping(spg_type_name="Medical.Disease") \
            .add_mapping_field("id", "id") \
            .add_mapping_field("name", "name") \
            .add_mapping_field("complication", "complication") \
            .add_mapping_field("commonSymptom", "commonSymptom") \
            .add_mapping_field("applicableDrug", "applicableDrug") \
            .add_mapping_field("department", "department") \
            .add_mapping_field("diseaseSite", "diseaseSite")

        """
        4. 定义输出到图谱
        """
        sink = KGWriter()

        """
        5. 定义builder_chain
        """
        return source >> extract >> mapping >> sink
