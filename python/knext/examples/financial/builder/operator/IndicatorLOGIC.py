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
        """
        record: {
        "input": "济南市财政收入质量及自给能力均较好，但土地出让收入大幅下降致综合财力明显下滑。济南市财政收入质量及自给能力均较好，但土地出让收入大幅下降致综合
    财力明显下滑。2022年济南市一般公共预算收入1,000.21亿元，扣除留 抵退税因素后同比增长8%，规模在山东省下辖地市中排名第2位；其中税收收入690.31亿元，税收占比69.02%；一般公共 预算支出1,260.23亿元，财政自给率79.37%。政
    府性基金收入547.29亿元，同比大幅下降48.38%，主要系土地出让收入 同比由966.74亿元降至453.74亿元；转移性收入285.78亿元（上年同期为233.11亿元）；综合财力约1,833.28亿元（上年 同期为2,301.02亿元）。"
        "ner": "[{'财政': ['财政收入质量', '财政自给能力', '土地出让收入', '一般公共预算收入', '留抵退税', '税收收入', '税收收入/一般公共预算收入', '一般公共预算支出', '财政自给率', '政府性基金收入', '转移性收入', '综合财力']}]",
        "rel": "[{'subject': '一般公共预算收入', 'predicate': '包含', 'object': ['税收收入']}, {'subject': '税收收入', 'predicate': '包含', 'object': ['留抵退税']}, {'subject': '政府性基金收入', 'predicate': '包含', 'object': ['土地出让收入', '转移性收入']}, {'subject': '综合财力', 'predicate': '包含', 'object': ['一般公共预算收入', '政府性基金收入']}]",
        "id": "财政",
        "name": "财政",
        "hasA": "财政收入质量,财政自给能力,土地出让收入....."
    }
        """
        return self.template\
            .replace("${input}", variables.get("input"))\
            .replace("${ner}", variables.get("ner"))\
            .replace("${rel}", variables.get("rel"))

    def parse_response(
            self, response: str
    ) -> List[SPGRecord]:
        """
        response: "[{\"subject\": \"土地出让收入大幅下降\", \"predicate\": \"顺承\", \"object\": [\"综合财力明显下滑\"]}]"
        """
        print("##########IndicatorLOGIC###########")
        response = "[{\"subject\": \"土地出让收入大幅下降\", \"predicate\": \"顺承\", \"object\": [\"综合财力明显下滑\"]}]"
        output_list = json.loads(response)

        logic_result = []
        for output in output_list:
            properties = {}
            for k, v in output.items():
                if k == "subject":
                    properties["id"] = k
                    properties["name"] = k
                elif k == "object":
                    properties["causeOf"] = ','.join(v)
            logic_result.append(SPGRecord("Financial.State", properties=properties))

        return logic_result
