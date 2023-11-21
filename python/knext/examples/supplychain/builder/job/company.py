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
from knext.core.builder.job.model.component import (
    SourceCsvComponent,
    SinkToKgComponent,
    EntityMappingComponent,
    RelationMappingComponent,
)
from schema.supplychain_schema_helper import SupplyChain


class Company(BuilderJob):
    parallelism = 6

    def build(self):
        source = SourceCsvComponent(
            local_path="./builder/job/data/Company.csv",
            columns=["id", "name", "products"],
            start_row=2,
        )

        mapping = (
            EntityMappingComponent(spg_type_name=SupplyChain.Company)
            .add_field("id", SupplyChain.Company.id)
            .add_field("name", SupplyChain.Company.name)
            .add_field("products", SupplyChain.Company.product)
        )

        sink = SinkToKgComponent()

        return source >> mapping >> sink


class CompanyUpdate(BuilderJob):
    parallelism = 6

    def build(self):
        source = SourceCsvComponent(
            local_path="./builder/job/data/CompanyUpdate.csv",
            columns=["id", "name", "products"],
            start_row=2,
        )

        mapping = (
            EntityMappingComponent(spg_type_name=SupplyChain.Company)
            .add_field("id", SupplyChain.Company.id)
            .add_field("name", SupplyChain.Company.name)
            .add_field("products", SupplyChain.Company.product)
        )

        sink = SinkToKgComponent()

        return source >> mapping >> sink


class CompanyFundTrans(BuilderJob):
    def build(self):
        source = SourceCsvComponent(
            local_path="./builder/job/data/Company_fundTrans_Company.csv",
            columns=["src", "dst", "transDate", "transAmt"],
            start_row=2,
        )

        mapping = (
            RelationMappingComponent(
                subject_name=SupplyChain.Company,
                predicate_name="fundTrans",
                object_name=SupplyChain.Company,
            )
            .add_field("src", "srcId")
            .add_field("dst", "dstId")
            .add_field("transDate", "transDate")
            .add_field("transAmt", "transAmt")
        )

        sink = SinkToKgComponent()

        return source >> mapping >> sink
