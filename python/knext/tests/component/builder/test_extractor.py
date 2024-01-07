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
import json
import os
from typing import Dict, List

from pytest_mock import mocker

from knext.client.operator import OperatorClient
from knext.component.builder import UserDefinedExtractor, LLMBasedExtractor
from knext.operator.op import ExtractOp, PromptOp
from knext.operator.spg_record import SPGRecord
from knext import rest


def get_op_config(op_name, params):
    operator_config = rest.OperatorConfig()
    operator_config.file_path = os.path.abspath(__file__)
    operator_config.module_path = "test_extractor"
    operator_config.class_name = op_name
    operator_config.method = "_handle"
    operator_config.params = params
    return operator_config


def get_user_defined_extractor_config(params):
    operator_config = get_op_config("TestExtractOp", params)
    node_config = rest.UserDefinedExtractNodeConfig(operator_config=operator_config)

    return node_config


def get_llm_based_extractor_config(nn_config):

    operator_config_1 = get_op_config("TestPromptOp1", None)
    operator_config_2 = get_op_config("TestPromptOp2", None)

    params = dict()
    params["model_config"] = json.dumps(nn_config)
    params["prompt_config"] = json.dumps(
        [
            OperatorClient().serialize(operator_config_1),
            OperatorClient().serialize(operator_config_2),
        ]
    )

    operator_config = get_op_config("_BuiltInOnlineExtractor", params)
    node_config = rest.UserDefinedExtractNodeConfig(operator_config=operator_config)

    return node_config


def get_test_extract_data():
    properties = {"phone": "+86-12345678", "addr": "China", "name": "taobao"}

    return SPGRecord("Company", properties)


class TestExtractOp(ExtractOp):
    def invoke(self, record: Dict[str, str]) -> List[SPGRecord]:
        spg_type = record["type"]
        properties = json.loads(record["properties"])
        return [SPGRecord(spg_type, properties)]


class TestPromptOp1(PromptOp):

    template = """
Question:${question}
Answer:
    """

    def build_prompt(self, variables: Dict[str, str]) -> str:
        return self.template.replace("${question}", variables.get("input"))

    def parse_response(self, response: str) -> List[SPGRecord]:
        pass


class TestPromptOp2(PromptOp):
    template = """
Question:${question}
Instruction:${instruction}
Answer:
    """

    def build_prompt(self, variables: Dict[str, str]) -> str:
        return self.template.replace("${question}", variables.get("input")).replace(
            "${instruction}", variables.get("TestPromptOp1")
        )

    def parse_response(self, response: str) -> List[SPGRecord]:
        pass


class MockLLMInvoker:
    @classmethod
    def from_config(cls):
        return cls()

    def remote_inference(self, data):
        return data


def test_user_defined_extractor():

    params = {"config1": "1"}
    extract_op = TestExtractOp(params=params)
    extract = UserDefinedExtractor(extract_op=extract_op)

    assert extract.id == id(extract)
    assert extract.name == "UserDefinedExtractor"
    assert extract.to_dict() == {"id": id(extract), "name": "UserDefinedExtractor"}
    assert extract.to_rest() == rest.Node(
        **extract.to_dict(), node_config=get_user_defined_extractor_config(params)
    )


def test_llm_based_extractor():

    nn_config = {"config1": "1", "config2": "2"}

    from nn4k.invoker import LLMInvoker

    extract = LLMBasedExtractor(
        llm=LLMInvoker.from_config(nn_config),
        prompt_ops=[TestPromptOp1(), TestPromptOp2],
    )

    assert extract.id == id(extract)
    assert extract.name == "LLMBasedExtractor"
    assert extract.to_dict() == {"id": id(extract), "name": "LLMBasedExtractor"}
    assert extract.to_rest() == rest.Node(
        **extract.to_dict(), node_config=get_llm_based_extractor_config(nn_config)
    )


# def test_builtin_online_extractor():
#
#     from knext.operator.builtin.online_runner import _BuiltInOnlineExtractor
#
#     extract_op = _BuiltInOnlineExtractor(params)
#     from nn4k.invoker import LLMInvoker
#     from_config = mocker.patch('LLMInvoker.from_config')
#     from_config.return_value = LLMInvoker()
#     remote_inference = mocker.patch("LLMInvoker.remote_inference")
#
#     monkeypatch.setattr(LLMInvoker, 'from_config', lambda: mock_llminvoker)
