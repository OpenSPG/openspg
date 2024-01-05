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

from typing import List

from knext.api.record import SPGRecord
from knext.api.operator import LinkOp
from knext.api.client import SearchClient

from schema.test_schema_helper import TEST


class Concept3LinkOp(LinkOp):
    bind_to = TEST.Concept3

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient(self.bind_to)

    def invoke(self, property: str, subject_record: SPGRecord) -> List[SPGRecord]:
        print("####################Concept3LinkOp#####################")
        print("Concept3LinkOp(Input): ")
        print("--------------------------------------------")
        print(f"property: {property}, subject_record: {subject_record}")

        linked_record = self.search_client.exact_search_by_property(property, TEST.Concept3.id)

        print("Concept3LinkOp(Output): ")
        print("--------------------------------------------")
        print([linked_record])

        return [linked_record]
