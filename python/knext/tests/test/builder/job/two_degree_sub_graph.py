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


from knext.client.model.builder_job import BuilderJob
from knext.api.component import CSVReader, SPGTypeMapping, KGWriter

from schema.test_schema_helper import TEST

from knext.component.builder.mapping import LinkingStrategyEnum, FusingStrategyEnum


class TwoDegreeSubGraph(BuilderJob):
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

        event_mapping = (
            SPGTypeMapping(
                spg_type_name=TEST.CenterEvent,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("id", TEST.CenterEvent.id)
            .add_property_mapping("id", TEST.CenterEvent.name)
            .add_property_mapping("text", TEST.CenterEvent.basicTextProperty)
            .add_property_mapping("integer", TEST.CenterEvent.basicIntegerProperty)
            .add_property_mapping("float", TEST.CenterEvent.basicFloatProperty)
            .add_property_mapping("standard", TEST.CenterEvent.standardProperty)
            .add_property_mapping(
                "concept",
                TEST.CenterEvent.conceptProperty,
                TEST.Concept1,
                LinkingStrategyEnum.IDEquals,
            )
            .add_property_mapping(
                "entity",
                TEST.CenterEvent.subject,
                TEST.Entity1,
                LinkingStrategyEnum.IDEquals,
            )
            .add_relation_mapping(
                "event",
                TEST.CenterEvent.eventRelation,
                TEST.CenterEvent,
                LinkingStrategyEnum.IDEquals,
            )
        )

        entity1_mapping = (
            SPGTypeMapping(
                spg_type_name=TEST.Entity1,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("entity", TEST.Entity1.id)
            .add_property_mapping("entity", TEST.Entity1.name)
            .add_relation_mapping(
                "entity_relation",
                TEST.Entity1.entityRelation,
                TEST.Entity2,
                LinkingStrategyEnum.IDEquals,
            )
            .add_predicting_relation(TEST.Entity1.predictRelation, TEST.Entity3)
        )

        concept1_mapping = (
            SPGTypeMapping(
                spg_type_name=TEST.Concept1,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("concept", TEST.Concept1.id)
            .add_property_mapping("concept", TEST.Concept1.name)
            .add_relation_mapping(
                "lead_to_concept2",
                TEST.Concept1.leadTo,
                TEST.Concept2,
                LinkingStrategyEnum.IDEquals,
            )
            .add_relation_mapping(
                "lead_to_concept3",
                TEST.Concept1.leadTo,
                TEST.Concept3,
                LinkingStrategyEnum.IDEquals,
            )
        )

        entity2_mapping = (
            SPGTypeMapping(
                spg_type_name=TEST.Entity2,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("entity_relation", TEST.Entity2.id)
            .add_property_mapping("entity_relation", TEST.Entity2.name)
        )

        entity3_mapping = (
            SPGTypeMapping(
                spg_type_name=TEST.Entity3,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("predict_relation", TEST.Entity3.id)
            .add_property_mapping("predict_relation", TEST.Entity3.name)
        )

        concept2_mapping = (
            SPGTypeMapping(
                spg_type_name=TEST.Concept2,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("lead_to_concept2", TEST.Concept3.id)
            .add_property_mapping("lead_to_concept2", TEST.Concept3.name)
        )

        concept3_mapping = (
            SPGTypeMapping(
                spg_type_name=TEST.Concept3,
                fusing_strategy=FusingStrategyEnum.Overwrite,
            )
            .add_property_mapping("lead_to_concept3", TEST.Concept3.id)
            .add_property_mapping("lead_to_concept3", TEST.Concept3.name)
        )

        sink = KGWriter()

        return (
            source
            >> [
                event_mapping,
                entity1_mapping,
                concept2_mapping,
                concept1_mapping,
                entity3_mapping,
                entity2_mapping,
                concept3_mapping,
            ]
            >> sink
        )
