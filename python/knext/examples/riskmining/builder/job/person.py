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
from knext.api.component import SPGTypeMapping
from knext.api.component import (
    CsvSourceReader,
    KGSinkWriter,
    RelationMapping
)
from knext.examples.riskmining.schema.riskmining_schema_helper import RiskMining


class Person(BuilderJob):
    def build(self):
        source = CsvSourceReader(
            local_path="./builder/job/data/Person.csv",
            columns=["id", "name", "age", "hasPhone"],
            start_row=2,
        )

        mapping = (
            SPGTypeMapping(spg_type_name=RiskMining.Person.__typename__)
            .add_field("id", RiskMining.Person.id)
            .add_field("name", RiskMining.Person.name)
            .add_field("age", RiskMining.Person.age)
            .add_field("hasPhone", RiskMining.Person.hasPhone)
        )

        sink = KGSinkWriter()

        return source >> mapping >> sink


class PersonFundTrans(BuilderJob):
    def build(self):
        source = CsvSourceReader(
            local_path="./builder/job/data/Person_fundTrans_Person.csv",
            columns=["src", "dst", "transDate", "transAmt"],
            start_row=2,
        )

        mapping = (
            RelationMapping(
                subject_name=RiskMining.Person.__typename__,
                predicate_name="fundTrans",
                object_name=RiskMining.Person.__typename__,
            )
            .add_field("src", "srcId")
            .add_field("dst", "dstId")
            .add_field("transDate", "transDate")
            .add_field("transAmt", "transAmt")
        )

        sink = KGSinkWriter()

        return source >> mapping >> sink


class PersonHasDevice(BuilderJob):
    def build(self):
        source = CsvSourceReader(
            local_path="./builder/job/data/Person_hasDevice_Device.csv",
            columns=["src", "dst"],
            start_row=2,
        )

        mapping = (
            RelationMapping(
                subject_name=RiskMining.Person.__typename__,
                predicate_name="hasDevice",
                object_name=RiskMining.Device.__typename__,
            )
            .add_field("src", "srcId")
            .add_field("dst", "dstId")
        )

        sink = KGSinkWriter()

        return source >> mapping >> sink


class PersonHoldShare(BuilderJob):
    def build(self):
        source = CsvSourceReader(
            local_path="./builder/job/data/Person_holdShare_Company.csv",
            columns=["src", "dst"],
            start_row=2,
        )

        mapping = (
            RelationMapping(
                subject_name=RiskMining.Person.__typename__,
                predicate_name="holdShare",
                object_name=RiskMining.Company.__typename__,
            )
            .add_field("src", "srcId")
            .add_field("dst", "dstId")
        )

        sink = KGSinkWriter()

        return source >> mapping >> sink
