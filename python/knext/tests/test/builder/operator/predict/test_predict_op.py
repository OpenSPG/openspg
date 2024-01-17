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

from knext.api.record import SPGRecord
from knext.api.operator import PredictOp

from schema.test_schema_helper import TEST


class TestPredictOp(PredictOp):
    bind_to = (TEST.Entity1, TEST.Entity1.predictRelation, TEST.Entity3)

    def invoke(self, subject_record: SPGRecord) -> List[SPGRecord]:
        print("####################TestPredictOp#####################")
        print("TestPredictOp(Input): ")
        print("----------------------")
        print(subject_record)

        predict_record = SPGRecord(
            spg_type_name=TEST.Entity3,
        )
        predict_record.upsert_properties(
            properties={
                "id": "entity3_312",
                "name": "entity3_312",
            }
        )
        print("TestPredictOp(Output): ")
        print("----------------------")
        print([predict_record])
        return [predict_record]
