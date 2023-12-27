import json
from typing import Dict, List

from knext.operator.op import PromptOp
from knext.operator.spg_record import SPGRecord


class IndicatorNER(PromptOp):
    template = """
请从以下文本中提取所有指标并给出指标类型，以json格式输出
#####
输出格式:
[{{"XXX": ["XXX", "XXX"]}}, {{"XXX": ["XXX", "XXX"]}}]
#####
文本: 
${input}
"""

    def build_prompt(self, variables: Dict[str, str]):
        template = self.template.replace("${input}", variables.get("input"))
        print("####################IndicatorNER(指标抽取)#####################")
        print("LLM(Input): ")
        print("----------------------")
        print(template)
        return template

    def parse_response(self, response: str) -> List[SPGRecord]:
        output_list = json.loads(response.replace("'", '"'))
        ner_result = []
        for output in output_list:
            for category, indicator_list in output.items():
                for indicator in indicator_list:
                    ner_result.append(
                        SPGRecord(
                            "Financial.Indicator",
                            properties={"id": indicator, "name": indicator},
                        )
                    )
        return ner_result
