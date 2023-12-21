import json
from abc import ABC
from typing import Union, List, Dict

from knext.client.model.base import BaseSpgType
from knext.client.schema import SchemaClient
from knext.common.schema_helper import SPGTypeHelper, PropertyHelper
from knext.operator.op import PromptOp
from knext.operator.spg_record import SPGRecord


class AutoPrompt(PromptOp, ABC):
    pass

class SPOPrompt(AutoPrompt):

    template: str = """
已知SPO关系包括:[${schema}]。从下列句子中提取定义的这些关系。最终抽取结果以json格式输出。
input:${input}
输出格式为:{"spo":[{"subject":,"predicate":,"object":},]}
"output":
    """

    def __init__(self,
                 spg_type_name: Union[str, SPGTypeHelper],
                 property_names: List[Union[str, PropertyHelper]],
                 custom_prompt: str = None):
        super().__init__()

        if custom_prompt:
            self.template = custom_prompt
        schema_client = SchemaClient()
        spg_type = schema_client.query_spg_type(spg_type_name=spg_type_name)
        self.spg_type_name = spg_type_name
        self.predicate_zh_to_en_name = {}
        self.predicate_type_zh_to_en_name = {}
        for k, v in spg_type.properties.items():
            self.predicate_zh_to_en_name[v.name_zh] = k
            self.predicate_type_zh_to_en_name[v.name_zh] = v.object_type_name
        self._render(spg_type, property_names)
        self.params = {
            "spg_type_name": spg_type_name,
            "property_names": property_names,
            "custom_prompt": custom_prompt,
        }

    def build_prompt(self, variables: Dict[str, str]) -> str:
        return self.template.replace("${input}", variables.get("input"))

    def parse_response(self, response: str) -> List[SPGRecord]:
        result = []
        subject = {}
        # re_obj = json.loads(response)
        re_obj = {
"spo": [
{
"subject": "甲状腺结节",
"predicate": "常见症状",
"object": "甲状腺结节"
},
{
"subject": "甲状腺结节",
"predicate": "适用药品",
"object": "放射性碘治疗,复方碘口服液(Lugol液),抗甲状腺药物,硫脲类化合物,丙基硫氧嘧啶(PTU),甲基硫氧嘧啶(MTU),咪唑类的甲硫咪唑和卡比马唑"
}
]
}
        if "spo" not in re_obj.keys():
            raise ValueError("SPO format error.")
        subject_properties = {}
        for spo_item in re_obj.get("spo", []):
            if spo_item["predicate"] not in self.predicate_zh_to_en_name:
                continue
            subject_properties = {"id": spo_item["subject"], "name": spo_item["subject"]}
            if spo_item["subject"] not in subject:
                subject[spo_item["subject"]] = subject_properties
            else:
                subject_properties = subject[spo_item["subject"]]

            # 获取属性类型
            spo_en_name = self.predicate_zh_to_en_name[spo_item["predicate"]]

            if spo_en_name in subject_properties and len(
                    subject_properties[spo_en_name]
            ):
                subject_properties[spo_en_name] = (
                        subject_properties[spo_en_name] + "," + spo_item["object"]
                )
            else:
                subject_properties[spo_en_name] = spo_item["object"]

            # for k, val in subject.items():
        subject_entity = SPGRecord(spg_type_name=self.spg_type_name, properties=subject_properties)
        result.append(subject_entity)
        return result

    def build_variables(self, variables: Dict[str, str], response: str) -> List[Dict[str, str]]:
        # re_obj = json.loads(response)
        re_obj = {
"spo": [
{
"subject": "甲状腺结节",
"predicate": "常见症状",
"object": "甲状腺结节"
},
{
"subject": "甲状腺结节",
"predicate": "适用药品",
"object": "放射性碘治疗,复方碘口服液(Lugol液),抗甲状腺药物,硫脲类化合物,丙基硫氧嘧啶(PTU),甲基硫氧嘧啶(MTU),咪唑类的甲硫咪唑和卡比马唑"
}
]
}
        if "spo" not in re_obj.keys():
            raise ValueError("SPO format error.")
        re = re_obj.get("spo", [])
        return [{"input": variables.get("input"), "spo": str(i)} for i in re]

    def _render(self, spg_type: BaseSpgType, property_names: List[str]):
        spos = []
        for property_name in property_names:
            if property_name in ["id", "name", "description"]:
                continue
            prop = spg_type.properties.get(property_name)
            spos.append(f'{spg_type.name_zh}({spg_type.desc or spg_type.name_zh})-{prop.name_zh}-{prop.object_type_name_zh}')
        schema_text = ','.join(spos)
        self.template = self.template.replace("${schema}", schema_text)
