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

from typing import List

from knext.builder.operator.spg_record import SPGRecord
from knext.builder.operator import FuseOp
from knext.common.search import SearchClient

from schema.test_schema_helper import TEST


class Entity2FuseOp(FuseOp):
    bind_to = TEST.Entity2

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient(self.bind_to)

    def link(self, subject_record: SPGRecord) -> SPGRecord:
        print("####################Entity2FuseOp#####################")
        print("Entity2FuseOp.link(Input): ")
        print("--------------------------------------------")
        print(subject_record)

        linked_record = self.search_client.exact_search(
            subject_record, TEST.Entity2.name
        )

        print("Entity2FuseOp.link(Output): ")
        print("--------------------------------------------")
        print(linked_record)
        return linked_record

    def merge(self, subject_record: SPGRecord, linked_record: SPGRecord) -> SPGRecord:
        print("Entity2FuseOp.merge(Input): ")
        print("--------------------------------------------")
        print(f"subject_record: {subject_record}, linked_record: {linked_record}")

        if linked_record:
            new_id = (
                subject_record.get_property("id")
                + "_"
                + linked_record.get_property("id")
            )
            subject_record.upsert_property(TEST.CenterEvent.id, new_id)

        print("Entity2FuseOp.merge(Output): ")
        print("--------------------------------------------")
        print(subject_record)
        return subject_record
