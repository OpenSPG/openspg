# -*- coding: utf-8 -*-
#
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

from knext.builder.operator import OneKE_EEPrompt
from knext.builder.model.builder_job import BuilderJob
from knext.builder.component import (
    CSVReader,
    LLMBasedExtractor,
    SPGTypeMapping,
    KGWriter,
)

try:
    from schema.oneke_schema_helper import OneKE
except:
    pass


class OneKE_EE(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/content.csv", columns=["input"], start_row=1
        )

        extract = LLMBasedExtractor(
            llm=NNInvoker.from_config("builder/model/oneke_infer.json"),
            prompt_ops=[
                OneKE_EEPrompt(
                    event_types=[OneKE.PublishMusicAlbum],
                )
            ],
        )

        mapping = SPGTypeMapping(spg_type_name=OneKE.PublishMusicAlbum)

        sink = KGWriter()

        return source >> extract >> mapping >> sink
