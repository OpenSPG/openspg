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

from knext.operator.spg_record import SPGRecord


def get_test_data():
    name = "Company"
    properties = {
        "phone": "+86-12345678",
        "addr": "China",
    }

    properties2 = {
        "phone": "+86-87654321",
        "addr": "CN",
    }
    return name, properties, properties2


def _test_spg_record_properties(record: SPGRecord):
    name, properties, properties2 = get_test_data()
    assert record.spg_type_name == name
    assert record.get_property("phone") == properties["phone"]
    assert record.get_property("addr") == properties["addr"]

    record.upsert_property("name", "taobao")
    assert record.get_property("name") == "taobao"

    record.update_properties(properties2)
    assert record.get_property("phone") == properties2["phone"]
    assert record.get_property("addr") == properties2["addr"]

    record.remove_property("name")
    assert record.get_property("name") is None

    record.remove_properties(["phone", "addr"])
    assert record.get_property("phone") is None
    assert record.get_property("addr") is None


def test_spg_record_properties():
    name, properties, properties2 = get_test_data()
    record = SPGRecord(name, properties)
    _test_spg_record_properties(record)


def test_spg_record_consturct():
    name, properties, properties2 = get_test_data()
    record = SPGRecord.from_dict({"spgTypeName": name, "properties": properties})
    _test_spg_record_properties(record)
    print(record)

    record = SPGRecord.from_dict({"spgTypeName": name, "properties": properties})
    record_dict = record.to_dict()
    assert record_dict["spgTypeName"] == record.spg_type_name
    print(record_dict["properties"])
    for k, v in record_dict["properties"].items():
        assert record.get_property(k) == v
