import json
from typing import Dict, List

from knext.operator.op import PromptOp
from knext.operator.spg_record import SPGRecord


class IndicatorLOGIC(PromptOp):
    template = """
请根据给定文本和文本中的指标及其指标关系，梳理逻辑链，以json格式输出
#####
输出格式:
[{"subject": "XXX", "predicate": "顺承", "object": ["XXX", "XXX"]}, {"subject": "XXX", "predicate": "顺承", "object": ["XXX", "XXX"]}]
文本: 
${input}
指标: 
${ner}
指标关系: 
${rel}
"""

    def build_prompt(self, variables: Dict[str, str]):
        template = (
            self.template.replace("${input}", variables.get("input"))
            .replace("${ner}", variables.get("IndicatorNER"))
            .replace("${rel}", variables.get("IndicatorREL"))
        )
        print("####################IndicatorLOGIC(状态逻辑抽取)#####################")
        print("LLM(Input): ")
        print("----------------------")
        print(template)
        return template

    def parse_response(self, response: str) -> List[SPGRecord]:
        output_list = json.loads(response)

        logic_result = []
        for output in output_list:
            properties = {}
            for k, v in output.items():
                if k == "subject":
                    properties["id"] = v
                    properties["name"] = v
                elif k == "object":
                    properties["causeOf"] = ",".join(v)
            logic_result.append(SPGRecord("Financial.State", properties=properties))
        return logic_result
