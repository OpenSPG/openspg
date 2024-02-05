# -*- coding: utf-8 -*-
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
from knext.builder.component.mapping import FusingStrategyEnum
from knext.builder.model.builder_job import BuilderJob
from knext.builder.component import CSVReader, SPGTypeMapping, KGWriter
from schema.test_schema_helper import TEST


class PreConcept(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/two_degree_sub_graph.csv",
            columns=[
                "id",
                "text",
                "integer",
                "float",
                "standard",
                "concept",
                "confidence_concept",
                "lead_to_concept2",
                "lead_to_concept3",
                "event",
                "confidence_event",
                "source_event",
                "entity",
                "entity_relation",
                "predict_relation",
            ],
            start_row=1,
        )

        concept_mapping_1 = (
            SPGTypeMapping(
                spg_type_name=TEST.Concept1,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("concept", TEST.Entity2.id)
            .add_property_mapping("concept", TEST.Entity2.name)
        )

        concept_mapping_2 = (
            SPGTypeMapping(
                spg_type_name=TEST.Concept2,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("lead_to_concept2", TEST.Entity3.id)
            .add_property_mapping("lead_to_concept2", TEST.Entity3.name)
        )

        sink = KGWriter()

        return source >> [concept_mapping_1, concept_mapping_2] >> sink
