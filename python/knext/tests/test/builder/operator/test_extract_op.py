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

from typing import List, Dict

from knext.api.record import SPGRecord
from knext.api.operator import ExtractOp


class TestExtractOp(ExtractOp):
    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def invoke(self, record: Dict[str, str]) -> List[Dict[str, str]]:

        center_event = SPGRecord(
            spg_type_name="TEST.CenterEvent",
            properties={
                "id": "TestEvent1",
                "name": "TestEvent1",
                "text": "text1",
                "integer": "123",
                "float": "4.56",
                "event": "TestEvent2",
                "entity": "TestEntity1",
                "standard": "20240101",
                "concept": "TestConcept1",
            },
            relations={},
        )

        event = SPGRecord(
            spg_type_name="TEST.CenterEvent",
            properties={
                "id": "TestEvent2",
                "name": "TestEvent2",
                "text": "text2",
                "integer": "234",
                "float": "5.67",
            },
        )

        entity = SPGRecord(
            spg_type_name="TEST.Entity1",
            properties={
                "id": "TestEntity1",
                "name": "TestEntity1",
                "entity": "TestEntity2",
            },
        )

        concept1 = SPGRecord(
            spg_type_name="TEST.Concept1",
            properties={
                "id": "TestConcept1",
                "name": "TestConcept1",
            },
            relations={"leadTo": "TestConcept2"},
        )

        concept2 = SPGRecord(
            spg_type_name="TEST.Concept2",
            properties={
                "id": "TestConcept2",
                "name": "TestConcept2",
            },
        )

        return [event, center_event, entity, concept]
