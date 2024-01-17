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

import json
from knext.operator.spg_record import SPGRecord
from knext.common.class_register import register_from_package
from knext.operator.base import BaseOp
from knext.operator.op import ExtractOp, LinkOp, FuseOp, PredictOp

register_from_package("./operators", BaseOp)
register_from_package("./operators", ExtractOp)
register_from_package("./operators", LinkOp)
register_from_package("./operators", FuseOp)
register_from_package("./operators", PredictOp)


def get_test_extract_data():
    record = {
        "type": "Company",
        "properties": '{"phone": "+86-12345678", "addr": "China"}',
    }
    return record


def get_test_record():
    properties = {"phone": "+86-12345678", "addr": "China", "name": "taobao"}

    return SPGRecord("Company", properties)


def test_get_op():
    op = BaseOp.by_name("DummyOp")()
    assert isinstance(
        op, BaseOp
    ), f"op should be subclass of BaseOp, got {type(BaseOp)}"


def test_extract_op():
    record = get_test_extract_data()
    op = ExtractOp.by_name("TestExtractOp")()
    op_out = op._handle(*(record,))
    spg_records = op_out["data"]
    assert isinstance(
        spg_records, list
    ), f"output should be list, got {type(spg_records)}"
    assert (
        len(spg_records) == 1
    ), f"expected output length should be 1, got {len(spg_records)}"
    assert spg_records[0]["spgTypeName"] == record["type"]
    properties = json.loads(record["properties"])
    for k, v in spg_records[0]["properties"].items():
        assert (
            properties[k] == v
        ), f"value of property {k} should be {properties[k]}, got {v}"


def test_link_op():
    subject_record = get_test_record()
    op = LinkOp.by_name("TestLinkOp")()
    name = subject_record.get_property("name")
    op_out = op._handle(*(name, subject_record.to_dict()))
    records = op_out["data"]
    assert (
        len(records) == op.num_outputs
    ), "length of output records should be {op.num_outputs}, got {len(records)}"
    records = [SPGRecord.from_dict(x) for x in records]
    for i in range(op.num_outputs):
        assert records[i].get_property("index") == str(
            i + 1
        ), f'value of property index should be {i+1}, got {records[i].get_property("index")}'

        idx_p = "indexed_property"
        assert (
            records[i].get_property("indexed_property") == f"{name}_{i+1}"
        ), f"value of property {idx_p} should be {name}_{i+1}, got {records[i].get_property(idx_p)}"


def test_fuse_op():
    subject_record = get_test_record()
    op = FuseOp.by_name("TestFuseOp")()
    name = subject_record.get_property("name")
    op_out = op._handle(*([subject_record.to_dict()],))
    print(f"op_out = {op_out}")
    records = op_out["data"]
    records = [SPGRecord.from_dict(x) for x in records]
    assert (
        len(records) == op.num_outputs
    ), "length of output records should be {op.num_outputs}, got {len(records)}"

    for i in range(op.num_outputs):
        assert records[i].get_property("index") == str(
            i + 1
        ), f'value of property index should be {i}, got {records[i].get_property("index")}'
        assert (
            records[i].get_property("name") == f"{name}{i+1}"
        ), f'value of property name should be f"{name}{i+1}", got {records[i].get_property("name")}'

        for k, v in subject_record.properties.items():
            if k == "name":
                continue
            assert (
                records[i].get_property(k) == v
            ), f"value of property {k} should be {v}, got {records[i].get_property(k)}"


def test_predict_op():
    subject_record = get_test_record()
    op = PredictOp.by_name("TestPredictOp")()
    name = subject_record.get_property("name")
    op_out = op._handle(*(subject_record.to_dict(),))
    print(f"op_out = {op_out}")
    records = op_out["data"]
    records = [SPGRecord.from_dict(x) for x in records]
    assert (
        len(records) == op.num_outputs
    ), "length of output records should be {op.num_outputs}, got {len(records)}"

    for i in range(op.num_outputs):
        assert records[i].get_property("index") == str(
            i + 1
        ), f'value of property index should be {i+1}, got {records[i].get_property("index")}'
        assert (
            records[i].get_property("name") == f"{name}{i+1}"
        ), f'value of property name should be f"{name}{i+1}", got {records[i].get_property("name")}'

        for k, v in subject_record.properties.items():
            if k == "name":
                continue
            assert (
                records[i].get_property(k) == v
            ), f"value of property {k} should be {v}, got {records[i].get_property(k)}"
