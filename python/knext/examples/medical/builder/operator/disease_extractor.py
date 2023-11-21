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

import json
from typing import List, Dict

import requests

from knext.core.builder.operator import KnowledgeExtractOp, Vertex
from knext.core.schema import Schema

prompt_template = {
    "ner": '已知实体类型(entity_type)包括:${schema}。假设你是一个专业的医学专家，请从下列文本中抽取所有实体(entity)。\n----文本----\n${input}\n----回答要求----\n1. 答案格式为：[{"entity": ,"entity_type": },]',
    "re": '假设你是一个专业的医学专家，请从文本中抽取关系。我们会首先提供文本，然后会提供知识图谱schema，再提供回答的具体要求，最后是一个举例。\n----文本----\n${input}\n----知识图谱schema----\n${schema}\n----回答要求----\n1. 答案格式为json格式：[{"subject":,"predicate":,"object":},]\n2. object要求简洁，必须是中文，如果object包含多个值请用英文逗号分隔；\n3. 每一条关系必须属于知识图谱schema。\n----举例----\n文本为：急性扁桃体炎通常伴有咽痛，声嘶，发热等症状。回答为：{"subject":"急性扁桃体炎","predicate":"症状","object":"咽痛,声嘶,发热"}',
    "convert": "请把上面的回答转成标准的json格式，并只返回json数据作为回答，不需要其他说明",
}


class DiseaseExtractor(KnowledgeExtractOp):
    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)
        schema = Schema()
        self.retry = 0
        self.spg_type = schema.query_spg_type("Medical.Disease")
        self.predicate_zh_to_en_name = {}
        self.predicate_type_zh_to_en_name = {}
        for k, v in self.spg_type.properties.items():
            self.predicate_zh_to_en_name[v.name_zh] = k
            self.predicate_type_zh_to_en_name[v.name_zh] = v.object_type_name

    def chat(self, llm_input, history=[]):
        data = {
            "prompt": llm_input,
            "history": json.dumps(history),
        }
        pull_response = requests.post("http://127.0.0.1:8888", json=data)
        return pull_response.text

    def get_re_prompt(self) -> str:
        """
        获取关系抽取Prompt
        """
        schema_text = ""
        for k, v in self.spg_type.properties.items():
            if v.name in ["id", "description"]:
                continue
            spo = '"subject":"{}","predicate":"{}","object":"{}"'.format(
                self.spg_type.name_zh, v.name_zh, v.object_type_name_zh
            )
            spo = "{" + spo + "}\n"
            schema_text = schema_text + spo

        prompt = prompt_template["re"].replace("${schema}", schema_text)
        return prompt

    def get_ner_prompt(self) -> str:
        """
        获取NER的Prompt
        """
        prompt = prompt_template["ner"].replace(
            "${schema}", f"[{self.spg_type.name}:{self.spg_type.name_zh}]"
        )
        return prompt

    def infer(self, prompt, extract_input):
        """
        大模型预测
        """
        llm_input = prompt.replace("${input}", extract_input)
        response = self.chat(llm_input)
        print(response)
        parsed_json = self.chat(
            prompt_template["convert"], history=[(llm_input, response)]
        )
        import re

        match = re.search(r"\[([^]]*)\]", parsed_json, re.DOTALL)
        if match:
            content = match.group(0)
            return content
        return None

    def ner_to_vertex(self, output):
        """
        识别关系抽取结果，并进行NER，再转换为加工链路协议格式
        """
        result = []
        subject = {}
        ner_prompt = self.get_ner_prompt()
        re_obj = json.loads(output)
        for spo_item in re_obj:
            # 过滤掉Schema定义以外的谓词
            if spo_item["predicate"] not in self.predicate_zh_to_en_name:
                continue

            subject_properties = {}
            if spo_item["subject"] not in subject:
                subject[spo_item["subject"]] = subject_properties
            else:
                subject_properties = subject[spo_item["subject"]]

            # 获取属性类型
            spo_en_name = self.predicate_zh_to_en_name[spo_item["predicate"]]
            spo_type = self.predicate_type_zh_to_en_name[spo_item["predicate"]]

            # 如果非文本，则进行NER，再生成object entity
            if spo_type != "Text":
                ner_output = self.infer(ner_prompt, spo_item["object"])
                ner_object = json.loads(ner_output)
                if len(ner_object):
                    ner_values = set()
                    for single in ner_object:
                        entity = single["entity"]
                        single_entity = Vertex()
                        single_entity.vertex_type = spo_type
                        single_entity.biz_id = entity
                        single_entity.update_property("name", entity)
                        result.append(single_entity)
                        ner_values.add(entity)
                    if spo_en_name in subject_properties and len(
                        subject_properties[spo_en_name]
                    ):
                        subject_properties[spo_en_name] = (
                            subject_properties[spo_en_name] + "," + ",".join(ner_values)
                        )
                    else:
                        subject_properties[spo_en_name] = ",".join(ner_values)
            else:
                if spo_en_name in subject_properties and len(
                    subject_properties[spo_en_name]
                ):
                    subject_properties[spo_en_name] = (
                        subject_properties[spo_en_name] + "," + spo_item["object"]
                    )
                else:
                    subject_properties[spo_en_name] = spo_item["object"]

        for k, val in subject.items():
            subject_entity = Vertex(k, "Medical.Disease", val)
            result.append(subject_entity)
        return result

    def eval(self, record: Dict[str, str]) -> List[Vertex]:
        try:
            content = record.get("content")
            output = self.infer(self.get_re_prompt(), content)
            vertexes = self.ner_to_vertex(output)
            return vertexes
        except Exception as e:
            print(e)
            if self.retry < 3:
                self.retry = self.retry + 1
                self.eval(record)


if __name__ == "__main__":
    a = DiseaseExtractor()
    v = Vertex()
    v.update_property(
        "content",
        "甲状腺结节是指在甲状腺内的肿块，可随吞咽动作随甲状腺而上下移动，是临床常见的病症，可由多种病因引起。临床上有多种甲状腺疾病，如甲状腺退行性变、炎症、自身免疫以及新生物等都可以表现为结节。甲状腺结节可以单发，也可以多发，多发结节比单发结节的发病率高，但单发结节甲状腺癌的发生率较高。患者通常可以选择在普外科，甲状腺外科，内分泌科，头颈外科挂号就诊。有些患者可以触摸到自己颈部前方的结节。在大多情况下，甲状腺结节没有任何症状，甲状腺功能也是正常的。甲状腺结节进展为其它甲状腺疾病的概率只有1%。有些人会感觉到颈部疼痛、咽喉部异物感，或者存在压迫感。当甲状腺结节发生囊内自发性出血时，疼痛感会更加强烈。治疗方面，一般情况下可以用放射性碘治疗，复方碘口服液(Lugol液)等，或者服用抗甲状腺药物来抑制甲状腺激素的分泌。目前常用的抗甲状腺药物是硫脲类化合物，包括硫氧嘧啶类的丙基硫氧嘧啶(PTU)和甲基硫氧嘧啶(MTU)及咪唑类的甲硫咪唑和卡比马唑。",
    )
    print(a.eval(v))
