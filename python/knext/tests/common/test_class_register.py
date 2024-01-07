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

import os
from knext.common.class_register import register_from_package
from knext.operator.base import BaseOp
from knext.operator.op import ExtractOp, LinkOp, FuseOp, PredictOp

PWD = os.path.dirname(__file__)


def test_register_from_package():
    pkg_dir = os.path.join(PWD, "../operators/operators")
    print(f"pkg_dir = {pkg_dir}")
    register_from_package(pkg_dir, BaseOp)

    op = BaseOp.by_name("DummyOp")()
    assert isinstance(op, BaseOp), "failed to register DummyOp"

    op = BaseOp.by_name("TestExtractOp")()
    assert isinstance(op, ExtractOp), "failed to register TestExtractOp"

    op = BaseOp.by_name("TestLinkOp")()
    assert isinstance(op, LinkOp), "failed to register TestLinkOp"

    op = BaseOp.by_name("TestFuseOp")()
    assert isinstance(op, FuseOp), "failed to register TestFuseOp"

    op = BaseOp.by_name("TestPredictOp")()
    assert isinstance(op, PredictOp), "failed to register TestPredictOp"


test_register_from_package()
