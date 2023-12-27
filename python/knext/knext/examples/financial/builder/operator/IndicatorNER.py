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
        return self.template.replace("${input}", variables.get("input"))

    def parse_response(self, response: str) -> List[SPGRecord]:
        response = "[{'财政': ['财政收入质量', '财政自给能力', '土地出让收入', '一般公共预算收入', '留抵退税', '税收收入', '税收收入/一般公共预算收入', '一般公共预算支出', '财政自给率', '政府性基金收入', '转移性收入', '综合财力']}]"

        print("##########IndicatorNER###########")
        print("IndicatorNER(Input): ")
        print(response)

        output_list = json.loads(response.replace("'", '"'))
        ner_result = []
        # IF hasA
        for output in output_list:
            # {'财政': ['财政收入....}
            for category, indicator_list in output.items():
                # '财政', ['财政收入....]
                for indicator in indicator_list:
                    ner_result.append(
                        SPGRecord(
                            "Financial.Indicator",
                            properties={"id": indicator, "name": indicator},
                        )
                    )
        print("IndicatorNER(Output): ")
        print(ner_result)
        print("##########IndicatorNER###########")
        return ner_result

    def build_next_variables(
        self, variables: Dict[str, str], response: str
    ) -> List[Dict[str, str]]:
        """
        response: "[{'subject': '一般公共预算收入', 'predicate': '包含', 'object': ['税收收入']}, {'subject': '税收收入', 'predicate': '包含', 'object': ['留抵退税']}, {'subject': '政府性基金收入', 'predicate': '包含', 'object': ['土地出让收入', '转移性收入']}, {'subject': '综合财力', 'predicate': '包含', 'object': ['一般公共预算收入', '政府性基金收入']}]"
        """
        response = "[{'财政': ['财政收入质量', '财政自给能力', '土地出让收入', '一般公共预算收入', '留抵退税', '税收收入', '税收收入/一般公共预算收入', '一般公共预算支出', '财政自给率', '政府性基金收入', '转移性收入', '综合财力']}]"

        return [{"input": variables["input"], "ner": response}]
