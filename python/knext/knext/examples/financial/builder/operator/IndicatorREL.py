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

from typing import Dict, List

from knext.api.operator import PromptOp


class IndicatorREL(PromptOp):
    template = """
请根据给定文本和文本中的指标，理解这些指标之间的关联关系，以json格式输出
#####
输出格式:
[{{"subject": "XXX", "predicate": "包含", "object": ["XXX", "XXX"]}}, {{"subject": "XXX", "predicate": "包含", "object": ["XXX", "XXX"]}}]
文本: 
${input}
指标: 
${ner}
"""

    def build_prompt(self, variables: Dict[str, str]) -> str:
        template = self.template.replace("${input}", variables.get("input")).replace(
            "${ner}", variables.get("IndicatorNER")
        )
        print("####################IndicatorREL(指标关系抽取)#####################")
        print("LLM(Input): ")
        print("----------------------")
        print(template)
        return template
