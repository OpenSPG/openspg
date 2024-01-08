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
import numpy as np
from typing import List, Dict
from knext.api.operator import PromptOp
from knext.api.record import SPGRecord


def get_mock_spg_records(size: int = 10):
    mock_data = [
        "财政收入质量",
        "财政自给能力",
        "土地出让收入",
        "一般公共预算收入",
        "留抵退税",
        "税收收入",
        "税收收入/一般公共预算收入",
        "一般公共预算支出",
        "财政自给率",
        "政府性基金收入",
        "转移性收入",
        "综合财力",
    ]
    output = []
    np.random.shuffle(mock_data)
    for data in mock_data[:size]:
        tmp = SPGRecord("Finance.Indicator")
        tmp.upsert_properties(                {
                    "id": data,
                    "name": data,
                })
        output.append(tmp)    
    return output


class IndicatorNERPrompt(PromptOp):
    template = """
请从以下文本中提取所有指标并给出指标类型，以json格式输出
#####
输出格式:
[{{"XXX": ["XXX", "XXX"]}}, {{"XXX": ["XXX", "XXX"]}}]
#####
文本: 
{input}
"""

    def build_prompt(self, variables: Dict[str, str]):
        return self.template.format(input=variables.get("input", ""))

    def parse_response(self, response: str) -> List[SPGRecord]:
        return get_mock_spg_records(5)


class IndicatorLinkPrompt(PromptOp):
    template = """
判断在指标列表{candidates}中，有无与指标{input}相同的指标名称，如果有，则返回相同指标名称，
没有则返回空字符串。
#####
输出格式:
{{"same_indicator": "XXX"}}
#####
文本: 
{input}
"""

    def build_prompt(self, variables: Dict[str, str]):
        return self.template.format(
            input=variables.get("input", ""),
            candidates=variables.get("candidates", [""]),
        )

    def parse_response(self, response: str) -> List[SPGRecord]:
        return get_mock_spg_records(1)


class IndicatorFusePrompt(PromptOp):
    template = """
判断在指标列表{candidates}中，有无与指标{input}相同的指标名称，如果有，则返回相同指标名称，
没有则返回空字符串。
#####
输出格式:
{{"same_indicator": "XXX"}}
#####
文本: 
{input}
"""

    def build_prompt(self, variables: Dict[str, str]):
        return self.template.format(
            input=variables.get("input", ""),
            candidates=variables.get("candidates", [""]),
        )

    def parse_response(self, response: str) -> List[SPGRecord]:
        return get_mock_spg_records(3)


class IndicatorPredictPrompt(PromptOp):
    template = """
请在你所知的指标关系中，寻找指标{input}的最多5个上位指标名称，如果有，则返回相同指标名称，
没有则返回空。
#####
输出格式:
{{"hypernym": ["XXX"]}}
"""

    def build_prompt(self, variables: Dict[str, str]):
        return self.template.format(
            input=variables.get("input", ""),
        )

    def parse_response(self, response: str) -> List[SPGRecord]:
        return get_mock_spg_records(5)
