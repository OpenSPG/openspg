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

from knext.builder.component import (
    CSVReader,
    KGWriter,
)
from knext.builder.component import SPGTypeMapping
from knext.builder.model.builder_job import BuilderJob
from knext.builder.component.mapping import RelationMapping

from schema.riskmining_schema_helper import RiskMining


class Company(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Company.csv",
            columns=["id", "name", "phone"],
            start_row=2,
        )

        mapping = (
            SPGTypeMapping(spg_type_name=RiskMining.Company)
            .add_property_mapping("id", RiskMining.Company.id)
            .add_property_mapping("name", RiskMining.Company.name)
            .add_property_mapping("phone", RiskMining.Company.hasPhone)
        )

        sink = KGWriter()

        return source >> mapping >> sink


class CompanyHasCert(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Company_hasCert_Cert.csv",
            columns=["src", "dst"],
            start_row=2,
        )

        mapping = (
            RelationMapping(
                subject_name=RiskMining.Company,
                predicate_name="hasCert",
                object_name=RiskMining.Cert,
            )
            .add_sub_property_mapping("src", "srcId")
            .add_sub_property_mapping("dst", "dstId")
        )

        sink = KGWriter()

        return source >> mapping >> sink
