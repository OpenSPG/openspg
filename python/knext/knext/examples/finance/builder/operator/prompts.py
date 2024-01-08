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
import copy
import json
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
        tmp.upsert_properties(
            {
                "id": data,
                "name": data,
            }
        )
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
判断在指标列表{candidates}中，有无与指标{input}相同的指标名称，如果有，则依照如下json格式
{{"same_indicator": "XXX"}}返回相同指标名称，没有则返回空字符串。注意返回结果一定要是可解析的json串。
"""

    def build_prompt(self, variables: Dict[str, str]):
        return self.template.format(
            input=variables.get("input", ""),
            candidates=variables.get("candidates", [""]),
        )

    def parse_response(self, response: str) -> List[SPGRecord]:
        tmp = json.loads(response)
        linked_indicator = tmp.get("same_indicator", "")
        if len(linked_indicator) > 0:
            output = SPGRecord("Finance.Indicator")
            output.upsert_property("id", linked_indicator)
            output.upsert_property("name", linked_indicator)
            return [output]
        return []


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


class EventExtractPrompt(PromptOp):
    template = {
        "input": "",
        "instruction": '你是专门进行事件提取的专家。请从input中抽取出符合schema定义的事件，不存在的事件返回空列表，不存在的论元返回空。请按照JSON字符串的格式回答。输出格式为:{"event":[{"event_type":,"arguments":{"":,},}]}',
        "schema": [
            {"arguments": ["时间", "地域", "指标名", "指标值", "指标趋势"], "event_type": "区域指标事件"}
        ],
        "schema description": {
            "区域经济指标事件": "指的是特定地区经济状况和发展水平的数据指标的相关事件。",
            "时间": "类型为时间，年/月/日",
            "地域": "类型为文本。指的是经济指标事件的范围，如全国、成都市、上海市等",
            "指标名": "类型为文本，是用于衡量特定地区经济状况和发展水平的一系列数据和指标",
            "指标值": "类型为数字, 代表指标名的数值",
            "指标趋势": "类型为文本，代表指标名的变化趋势，如果不存在可以为空",
        },
    }

    def build_prompt(self, variables: Dict[str, str]):
        tmp = copy.deepcopy(self.template)
        tmp["input"] = variables["input"]
        return json.dumps(tmp)

    def parse_response(self, response: str) -> List[SPGRecord]:
        records = json.loads(response)
        output = []
        for record in records:
            tmp = SPGRecord("Finance.IndicatorEvent")
            tmp.upsert_property("indicator", record["arguments"].get("指标名", ""))
            tmp.upsert_property("value", record["arguments"].get("指标值", ""))
            tmp.upsert_property("date", record["arguments"].get("时间", ""))
            tmp.upsert_property("trend", record["arguments"].get("指标趋势", ""))
            output.append(tmp)
        return output
