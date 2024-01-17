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
import requests
from typing import List
from knext.api.operator import LinkOp
from knext.api.record import SPGRecord
from knext.api.client import SearchClient

from schema.finance_schema_helper import Finance
from nn4k.invoker import NNInvoker


class IndicatorLinkOp(LinkOp):
    bind_to = Finance.Indicator

    def __init__(self):
        super().__init__()
        from builder.operator.prompts import IndicatorLinkPrompt

        self.prompt_op = IndicatorLinkPrompt()
        self.search_client = SearchClient(self.bind_to)

    def invoke(self, property: str, subject_record: SPGRecord) -> List[SPGRecord]:
        # Retrieve relevant indicators from KG based on indicator name
        name = property
        recall_records = self.search_client.fuzzy_search_by_property(
            property, "name", size=3
        )
        if len(recall_records) == 0:
            print("no indicators recalled")
            tmp = SPGRecord("Finance.Indicator")
            tmp.upsert_property("id", property)
            tmp.upsert_property("name", name)
            return [tmp]
        print(f"recalled indicators: {recall_records}")
        return recall_records
