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
import re
from abc import ABC
from typing import List, Dict, Tuple

from knext.client.schema import SchemaClient
from knext.common.schema_helper import SPGTypeName, PropertyName, RelationName
from knext.operator.op import PromptOp
from knext.operator.spg_record import SPGRecord


class AutoPrompt(PromptOp, ABC):
    pass


class REPrompt(AutoPrompt):

    template: str = """
已知SPO关系包括:${schema}
从下列句子中提取定义的这些关系。最终抽取结果以json格式输出，且predicate必须在[${predicate}]内。
input:${input}
输出格式为:{"spo":[{"subject":,"predicate":,"object":},]}
"output":
    """

    def __init__(
        self,
        spg_type_name: SPGTypeName,
        property_names: List[PropertyName] = None,
        relation_names: List[Tuple[RelationName, SPGTypeName]] = None,
        custom_prompt: str = None,
    ):
        super().__init__()

        self.spg_type_name = spg_type_name
        if custom_prompt:
            self.template = custom_prompt
        if not property_names:
            property_names = []
        if not relation_names:
            relation_names = []

        self.property_names = property_names
        self.relation_names = relation_names

        self._init_render_variables()
        self._render()

        self.params = {
            "spg_type_name": spg_type_name,
            "property_names": property_names,
            "relation_names": relation_names,
            "custom_prompt": custom_prompt,
        }

    def build_prompt(self, variables: Dict[str, str]) -> str:
        return self.template.replace("${input}", variables.get("input"))

    def parse_response(self, response: str) -> List[SPGRecord]:
        response = """
        {
  "spo": [
    {
      "subject": "甲状腺结节",
      "predicate": "并发症",
      "object": "甲状腺癌"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "常见症状",
      "object": "颈部疼痛"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "常见症状",
      "object": "咽喉部异物感"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "常见症状",
      "object": "压迫感"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "适用药品",
      "object": "放射性碘治疗"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "适用药品",
      "object": "复方碘口服液(Lugol液)"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "适用药品",
      "object": "丙基硫氧嘧啶(PTU)"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "适用药品",
      "object": "甲基硫氧嘧啶(MTU)"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "适用药品",
      "object": "甲硫咪唑"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "适用药品",
      "object": "卡比马唑"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "就诊科室",
      "object": "普外科"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "就诊科室",
      "object": "甲状腺外科"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "异常指征",
      "object": "血压上升"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "就诊科室",
      "object": "头颈外科"
    },
    {
      "subject": "甲状腺结节",
      "predicate": "发病部位",
      "object": "甲状腺"
    }
  ]
}
        """
        if isinstance(response, list) and len(response) > 0:
            response = response[0]
        re_obj = json.loads(response)
        if "spo" not in re_obj.keys():
            raise ValueError("SPO format error.")
        subject_records = {}
        result = []
        for spo_item in re_obj.get("spo", []):
            if any(k not in spo_item for k in ["subject", "predicate", "object"]):
                continue
            s, p_zh, o = spo_item["subject"], spo_item["predicate"], spo_item["object"]
            if s not in subject_records:
                subject_records[s] = SPGRecord(spg_type_name=self.spg_type_name).upsert_property("id", s).upsert_property("name", s)
            if p_zh in self.property_info_zh:
                p, _, o_type = self.property_info_zh[p_zh]
                o_list = re.split("[,，、;；]", o)
                result.extend([SPGRecord(o_type).upsert_property("id", _o).upsert_property("name", _o) for _o in o_list])
                o = subject_records[s].get_property(p)
                o = ','.join([o] + o_list) if o else ','.join(o_list)
                subject_records[s].upsert_property(p, o)
            elif p_zh in self.relation_info_zh:
                p, _, o_type = self.relation_info_zh[p_zh]
                o_list = re.split("[,，、;；]", o)
                result.extend([SPGRecord(o_type).upsert_property("id", _o).upsert_property("name", _o) for _o in o_list])
                o = subject_records[s].get_relation(p, o_type, "")
                o = ','.join([o] + o_list) if o else ','.join(o_list)
                subject_records[s].upsert_relation(p, o_type, o)
            else:
                continue

        for subject_record in subject_records.values():
            result.append(subject_record)
        return result

    def _render(self):
        spo_infos = []
        predicates = []
        duplicate_types = set()
        duplicate_predicates = set()
        for _prop in self.property_names:
            s_name_zh, s_desc = self.spg_type_schema_info_en.get(self.spg_type_name)
            s_desc = (s_desc or s_name_zh) if self.spg_type_name not in duplicate_types else None
            s_info = (s_name_zh or "") + (f'({s_desc})' if s_desc else "")
            p_name_zh, p_desc, o_type = self.property_info_en.get(_prop)
            p_desc = (p_desc or p_name_zh) if _prop not in duplicate_predicates else None
            p_info = (p_name_zh or "") + (f'({p_desc})' if p_desc else "")
            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            o_desc = (o_desc or o_name_zh) if o_type not in duplicate_types else None
            o_info = (o_name_zh or "") + (f'({o_desc})' if o_desc else "")
            spo_infos.append(f'{s_info}-{p_info}-{o_info}')
            duplicate_predicates.add(_prop)
            duplicate_types.update([self.spg_type_name, o_type])
            predicates.append(p_name_zh)
        for _rel, o_type in self.relation_names:
            s_name_zh, s_desc = self.spg_type_schema_info_en.get(self.spg_type_name)
            s_desc = (s_desc or s_name_zh) if self.spg_type_name not in duplicate_types else None
            s_info = (s_name_zh or "") + (f'({s_desc})' if s_desc else "")
            p_name_zh, p_desc, _ = self.relation_info_en.get(_rel)
            p_desc = (p_desc or p_name_zh) if _rel not in duplicate_predicates else None
            p_info = (p_name_zh or "") + (f'({p_desc})' if p_desc else "")
            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            o_desc = (o_desc or o_name_zh) if o_type not in duplicate_types else None
            o_info = (o_name_zh or "") + (f'({o_desc})' if o_desc else "")
            spo_infos.append(f'{s_info}-{p_info}-{o_info}')
            duplicate_predicates.add(_rel)
            duplicate_types.update([self.spg_type_name, o_type])
            predicates.append(p_name_zh)
        schema_text = "\n[" + ",\n".join(spo_infos) + "]"
        predicate_text = ','.join(predicates)
        self.template = self.template.replace("${schema}", schema_text).replace("${predicate}", predicate_text)

    def _init_render_variables(self):
        schema_session = SchemaClient().create_session()
        spg_type = schema_session.get(spg_type_name=self.spg_type_name)
        self.property_info_en = {}
        self.property_info_zh = {}
        self.relation_info_en = {}
        self.relation_info_zh = {}
        self.spg_type_schema_info_en = {"Text": ("文本", None), "Integer": ("整型", None), "Float": ("浮点型", None)}
        self.spg_type_schema_info_zh = {"文本": ("Text", None), "整型": ("Integer", None), "浮点型": ("Float", None)}
        for _rel in spg_type.relations.values():
            if _rel.is_dynamic:
                continue
            self.relation_info_zh[_rel.name_zh] = (_rel.name, _rel.desc, _rel.object_type_name)
            self.relation_info_en[_rel.name] = (_rel.name_zh, _rel.desc, _rel.object_type_name)
        for _prop in spg_type.properties.values():
            self.property_info_zh[_prop.name_zh] = (_prop.name, _prop.desc, _prop.object_type_name)
            self.property_info_en[_prop.name] = (_prop.name_zh, _prop.desc, _prop.object_type_name)
        for _type in schema_session.spg_types.values():
            if _type.name in ["Text", "Integer", "Float"]:
                continue
            self.spg_type_schema_info_zh[_type.name_zh] = (_type.name, _type.desc)
            self.spg_type_schema_info_en[_type.name] = (_type.name_zh, _type.desc)
