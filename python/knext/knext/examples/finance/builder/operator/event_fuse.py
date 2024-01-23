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
from knext.builder.operator import FuseOp
from knext.builder.operator.spg_record import SPGRecord
from knext.common.search import SearchClient

from schema.finance_schema_helper import Finance


class EventFuseOp(FuseOp):
    bind_to = Finance.IndicatorEvent

    def __init__(self):
        super().__init__()

        self.search_client = SearchClient(Finance.IndicatorEvent)

    def link(self, subject_record: SPGRecord) -> SPGRecord:
        # Retrieve relevant events from KG based on event name
        recall_record = self.search_client.exact_search(subject_record, "name")
        if not recall_record:
            return subject_record
        return recall_record

    def merge(self, subject_record: SPGRecord, linked_record: SPGRecord) -> SPGRecord:
        # Merge relations from event to subject.
        subjects = subject_record.get_property("subject", "").split(
            ","
        ) + linked_record.get_property("subject", "").split(",")
        subjects = ",".join(list(set(subjects)))
        linked_record.upsert_property("subject", subjects)

        return linked_record
