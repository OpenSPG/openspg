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

from knext.client.search import SearchClient
from knext.operator.op import FuseOp
from knext.operator.spg_record import SPGRecord

from schema.finance_schema_helper import Finance


class IndicatorFuse(FuseOp):
    bind_to = Finance.Indicator

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient(self.bind_to)

    def link(self, subject_record: SPGRecord) -> SPGRecord:
        linked_record = self.search_client.exact_search(subject_record, "name")

        return linked_record

    def merge(
            self, subject_record: SPGRecord, linked_record: SPGRecord
    ) -> SPGRecord:
        if linked_record:
            return linked_record
        return subject_record
