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
from knext.api.component import SPGTypeMapping
from knext.api.component import CSVReader, KGWriter
from schema.riskmining_schema_helper import RiskMining

from knext.component.builder.mapping import RelationMapping


class Person(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Person.csv",
            columns=["id", "name", "age", "hasPhone"],
            start_row=2,
        )

        mapping = (
            SPGTypeMapping(spg_type_name=RiskMining.Person)
            .add_property_mapping("id", RiskMining.Person.id)
            .add_property_mapping("name", RiskMining.Person.name)
            .add_property_mapping("age", RiskMining.Person.age)
            .add_property_mapping("hasPhone", RiskMining.Person.hasPhone)
        )

        sink = KGWriter()

        return source >> mapping >> sink


class PersonFundTrans(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Person_fundTrans_Person.csv",
            columns=["src", "dst", "transDate", "transAmt"],
            start_row=2,
        )

        mapping = (
            RelationMapping(
                subject_name=RiskMining.Person,
                predicate_name="fundTrans",
                object_name=RiskMining.Person,
            )
            .add_sub_property_mapping("src", "srcId")
            .add_sub_property_mapping("dst", "dstId")
            .add_sub_property_mapping("transDate", "transDate")
            .add_sub_property_mapping("transAmt", "transAmt")
        )
        sink = KGWriter()

        return source >> mapping >> sink


class PersonHasDevice(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Person_hasDevice_Device.csv",
            columns=["src", "dst"],
            start_row=2,
        )

        mapping = (
            RelationMapping(
                subject_name=RiskMining.Person,
                predicate_name="hasDevice",
                object_name=RiskMining.Device,
            )
            .add_sub_property_mapping("src", "srcId")
            .add_sub_property_mapping("dst", "dstId")
        )

        sink = KGWriter()

        return source >> mapping >> sink


class PersonHoldShare(BuilderJob):
    def build(self):
        source = CSVReader(
            local_path="./builder/job/data/Person_holdShare_Company.csv",
            columns=["src", "dst"],
            start_row=2,
        )

        mapping = (
            RelationMapping(
                subject_name=RiskMining.Person,
                predicate_name="holdShare",
                object_name=RiskMining.Company,
            )
            .add_sub_property_mapping("src", "srcId")
            .add_sub_property_mapping("dst", "dstId")
        )

        sink = KGWriter()

        return source >> mapping >> sink
