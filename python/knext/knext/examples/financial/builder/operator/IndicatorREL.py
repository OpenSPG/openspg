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
