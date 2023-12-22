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
{input}
"""

    def build_prompt(self, variables: Dict[str, str]):
        return self.template.replace("${input}", variables.get("input"))

    def parse_response(
        self, response: str
    ) -> List[SPGRecord]:
        # output_list = json.loads(response)
        #
        # ner_result = []
        # # IF hasA
        # for output in output_list:
        #     # {'财政': ['财政收入....}
        #     for k, v in output.items():
        #         # '财政', ['财政收入....]
        #         ner_result.append(SPGRecord("FEL.Indicator", properties={"id": k, "name": k, "hasA": ','.join(v)}))
        #
        # # ELSE isA
        # # TODO 通过属性isA支持
        # for output in output_list:
        #     # {'财政': ['财政收入....}
        #     for k, v in output.items():
        #         # '财政', ['财政收入....]
        #         for _v in v:
        #             # '财政收入....'
        #             ner_result.append(SPGRecord("FEL.Indicator", properties={"id": f'{k}-{_v}', "name": _v}))
        print("##########IndicatorNER###########")
        ner_result = [SPGRecord(spg_type_name="Financial.Indicator", properties={"id": "土地出让收入", "name": "土地出让收入"})]
        print(ner_result)
        print("##########IndicatorNER###########")
        return ner_result

    def build_next_variables(
            self, variables: Dict[str, str], response: str
    ) -> List[Dict[str, str]]:
        """
        response: "[{'subject': '一般公共预算收入', 'predicate': '包含', 'object': ['税收收入']}, {'subject': '税收收入', 'predicate': '包含', 'object': ['留抵退税']}, {'subject': '政府性基金收入', 'predicate': '包含', 'object': ['土地出让收入', '转移性收入']}, {'subject': '综合财力', 'predicate': '包含', 'object': ['一般公共预算收入', '政府性基金收入']}]"
        """
        response = ""
        return [{"input": variables["input"], "ner": response}]
