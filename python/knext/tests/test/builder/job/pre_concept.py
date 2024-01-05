# -*- coding: utf-8 -*-

from knext.client.model.builder_job import BuilderJob
from knext.api.component import CSVReader, SPGTypeMapping, KGWriter
from schema.test_schema_helper import TEST


class PreConcept(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/data_constructor.csv",
            columns=[
                "id",
                "text",
                "integer",
                "float",
                "standard",
                "concept",
                "lead_to_concept",
                "relevant_event_id",
                "subject_entity_id",
                "subject_relation_id",
            ],
            start_row=1,
        )

        concept_mapping_1 = (
            SPGTypeMapping(spg_type_name=TEST.Concept1)
            .add_mapping_field("concept", TEST.Entity2.id)
            .add_mapping_field("concept", TEST.Entity2.name)
        )

        concept_mapping_2 = (
            SPGTypeMapping(spg_type_name=TEST.Concept2)
            .add_mapping_field("lead_to_concept", TEST.Entity3.id)
            .add_mapping_field("lead_to_concept", TEST.Entity3.name)
        )

        sink = KGWriter()

        return source >> [concept_mapping_1, concept_mapping_2] >> sink
