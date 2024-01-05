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
from knext.operator.op import PredictOp
from knext.operator.spg_record import SPGRecord

from schema.finance_schema_helper import Finance


class IndicatorPredict(PredictOp):

    bind_to = (Finance.State, Finance.State.derivedFrom, Finance.Indicator)

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient(Finance.Indicator)

    def invoke(self, subject_record: SPGRecord) -> List[SPGRecord]:
        recall_records = self.search_client.fuzzy_search(subject_record, "name")
        if recall_records:
            return [recall_records[0]]
        return []
