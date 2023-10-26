# -*- coding: utf-8 -*-
#
#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

from knext.core.builder.job.builder import BuilderJob
from knext.core.builder.job.model.component import SourceCsvComponent, SPGMappingComponent, SinkToKgComponent
from schema.medical_schema_helper import Medical


class Disease(BuilderJob):

    def build(self):
        """
        1. 定义输入源，CSV文件，其中CSV文件每一行为一段文本
        """
        source = SourceCsvComponent(
            local_path="./builder/job/data/Disease.csv",
            columns=["content"],
            start_row=2
        )

        """
        2. 指定SPG知识映射组件，设置抽取算子，从长文本中抽取多种实体类型
        """
        mapping = SPGMappingComponent(
            spg_type_name=Medical.Disease
        ).set_operator("DiseaseExtractor")

        """
        3. 定义输出到图谱
        """
        sink = SinkToKgComponent()

        """
        4. 完整Pipeline定义
        """

        return source >> mapping >> sink
