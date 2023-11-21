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

from knext.core.builder.job.builder import BuilderJob
from knext.core.builder.job.model.component import EntityMappingComponent
from knext.core.builder.job.model.component import (
    SourceCsvComponent,
    SinkToKgComponent,
    RelationMappingComponent,
)
from schema.riskmining_schema_helper import RiskMining


class Person(BuilderJob):
    def build(self):
        source = SourceCsvComponent(
            local_path="./builder/job/data/Person.csv",
            columns=["id", "name", "age", "hasPhone"],
            start_row=2,
        )

        mapping = (
            EntityMappingComponent(spg_type_name=RiskMining.Person)
            .add_field("id", RiskMining.Person.id)
            .add_field("name", RiskMining.Person.name)
            .add_field("age", RiskMining.Person.age)
            .add_field("hasPhone", RiskMining.Person.hasPhone)
        )

        sink = SinkToKgComponent()

        return source >> mapping >> sink


class PersonFundTrans(BuilderJob):
    def build(self):
        source = SourceCsvComponent(
            local_path="./builder/job/data/Person_fundTrans_Person.csv",
            columns=["src", "dst", "transDate", "transAmt"],
            start_row=2,
        )

        mapping = (
            RelationMappingComponent(
                subject_name=RiskMining.Person,
                predicate_name="fundTrans",
                object_name=RiskMining.Person,
            )
            .add_field("src", "srcId")
            .add_field("dst", "dstId")
            .add_field("transDate", "transDate")
            .add_field("transAmt", "transAmt")
        )

        sink = SinkToKgComponent()

        return source >> mapping >> sink


class PersonHasDevice(BuilderJob):
    def build(self):
        source = SourceCsvComponent(
            local_path="./builder/job/data/Person_hasDevice_Device.csv",
            columns=["src", "dst"],
            start_row=2,
        )

        mapping = (
            RelationMappingComponent(
                subject_name=RiskMining.Person,
                predicate_name="hasDevice",
                object_name=RiskMining.Device,
            )
            .add_field("src", "srcId")
            .add_field("dst", "dstId")
        )

        sink = SinkToKgComponent()

        return source >> mapping >> sink


class PersonHoldShare(BuilderJob):
    def build(self):
        source = SourceCsvComponent(
            local_path="./builder/job/data/Person_holdShare_Company.csv",
            columns=["src", "dst"],
            start_row=2,
        )

        mapping = (
            RelationMappingComponent(
                subject_name=RiskMining.Person,
                predicate_name="holdShare",
                object_name=RiskMining.Company,
            )
            .add_field("src", "srcId")
            .add_field("dst", "dstId")
        )

        sink = SinkToKgComponent()

        return source >> mapping >> sink
