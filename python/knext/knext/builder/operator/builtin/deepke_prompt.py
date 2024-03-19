#
# Copyright 2023 OpenSPG Authors
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
from typing import List, Dict, Tuple, Any
from collections import defaultdict

from knext.schema.client import SchemaClient
from knext.schema.model.schema_helper import SPGTypeName, PropertyName, RelationName
from knext.builder.operator.op import PromptOp
from knext.builder.operator.spg_record import SPGRecord
from knext.builder.operator.builtin.auto_prompt import AutoPrompt
import uuid


class DeepKE_REPrompt(AutoPrompt):
    template_zh: str = "你是专门进行关系抽取的专家。请从input中抽取出符合schema定义的关系三元组，不存在的关系返回空列表。请按照JSON字符串的格式回答。"
    template_en: str = "You are an expert in relationship extraction. Please extract relationship triples that match the schema definition from the input. Return an empty list for relationships that do not exist. Please respond in the format of a JSON string."

    def __init__(
        self,
        spg_type_name: SPGTypeName,
        property_names: List[PropertyName] = None,
        relation_names: List[Tuple[RelationName, SPGTypeName]] = None,
        custom_prompt: str = None,
        language: str = 'zh',
    ):
        super().__init__()
        self.split_num = 4
        self.spg_type_name = spg_type_name
        if custom_prompt:
            self.template = custom_prompt
        if language == "zh":
            self.template = self.template_zh
        else:
            self.template = self.template_en
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


    def build_prompt(self, variables: Dict[str, str]) -> List[str]:
        instructions = []
        for schema in self.schema_list:
            instructions.append(
                json.dumps({
                    "instruction": self.template,
                    "schema": schema,  # [{'subject_type': '人物', 'predicate': '出生地', 'object_type': '地点'}, {'subject_type': '人物', 'predicate': '出生日期', 'object_type': '日期]
                    "input": variables.get("input"),
                },ensure_ascii=False,)
            )
        return instructions


    def parse_response(self, response: str) -> List[SPGRecord]:
        if isinstance(response, list) and len(response) > 0:
            response = response[0]
        try:
            re_obj = json.loads(response)
        except json.decoder.JSONDecodeError:
            raise ValueError("DeepKE_REPrompt response JSONDecodeError error.")
        if type(re_obj) != dict:
            raise ValueError("DeepKE_REPrompt response type error.")
        
        spo_records = []
        for p_zh, values in re_obj.items():
            if type(p_zh) != str or type(values) != list:
                print(f"{p_zh} != str or {values} != list")
                continue
            for value in values:
                if type(value) != dict:
                    print(f"{value} != dict")
                    continue
                s, o = values.get('subject', ''), values.get('object', '')
                spo_records.append((s, p_zh, o))   

        subject_records = {}
        result = []
        for s, p_zh, o in spo_records: 
            if s not in subject_records:
                subject_records[s] = (
                    SPGRecord(spg_type_name=self.spg_type_name)
                    .upsert_property("id", s)
                    .upsert_property("name", s)
                )
            if p_zh in self.property_info_zh:
                p, _, o_type = self.property_info_zh[p_zh]
                o_list = re.split("[,，、;；]", o)
                result.extend(
                    [
                        SPGRecord(o_type)
                        .upsert_property("id", _o)
                        .upsert_property("name", _o)
                        for _o in o_list
                    ]
                )
                o = subject_records[s].get_property(p)
                o = ",".join([o] + o_list) if o else ",".join(o_list)
                subject_records[s].upsert_property(p, o)
            elif p_zh in self.relation_info_zh:
                p, _, o_type = self.relation_info_zh[p_zh]
                if "." not in o_type or "STD." in o_type:
                    continue
                o_list = re.split("[,，、;；]", o)
                result.extend(
                    [
                        SPGRecord(o_type)
                        .upsert_property("id", _o)
                        .upsert_property("name", _o)
                        for _o in o_list
                    ]
                )
                o = subject_records[s].get_relation(p, o_type, "")
                o = ",".join([o] + o_list) if o else ",".join(o_list)
                subject_records[s].upsert_relation(p, o_type, o)
            else:
                continue

        for subject_record in subject_records.values():
            result.append(subject_record)
        return result
    

    def multischema_split_by_num(self, schemas: List[Any]):
        negative_length = max(len(schemas) // self.split_num, 1) * self.split_num
        total_schemas = []
        for i in range(0, negative_length, self.split_num):
            total_schemas.append(schemas[i:i+self.split_num])

        remain_len = max(1, self.split_num // 2)
        tmp_schemas = schemas[negative_length:]
        if len(schemas) - negative_length >= remain_len and len(tmp_schemas) > 0:
            total_schemas.append(tmp_schemas)
        elif len(tmp_schemas) > 0:
            total_schemas[-1].extend(tmp_schemas)
        return total_schemas


    def _render(self):
        spo_infos = []
        predicates = []
        duplicate_types = set()
        duplicate_predicates = set()
        for _prop in self.property_names:
            s_name_zh, s_desc = self.spg_type_schema_info_en.get(self.spg_type_name)
            s_desc = (
                (s_desc or s_name_zh)
                if self.spg_type_name not in duplicate_types
                else None
            )
            s_info = (s_name_zh or "") + (f"({s_desc})" if s_desc else "")
            
            p_name_zh, p_desc, o_type = self.property_info_en.get(_prop)
            p_desc = (
                (p_desc or p_name_zh) if _prop not in duplicate_predicates else None
            )
            p_info = (p_name_zh or "") + (f"({p_desc})" if p_desc else "")
            
            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            o_desc = (o_desc or o_name_zh) if o_type not in duplicate_types else None
            o_info = (o_name_zh or "") + (f"({o_desc})" if o_desc else "")
            
            spo_infos.append({"subject_type":s_name_zh, "predicate":p_name_zh, "object_type":o_name_zh})
            duplicate_predicates.add(_prop)
            duplicate_types.update([self.spg_type_name, o_type])
            predicates.append(p_name_zh)

        for _rel, o_type in self.relation_names:
            s_name_zh, s_desc = self.spg_type_schema_info_en.get(self.spg_type_name)
            s_desc = (
                (s_desc or s_name_zh)
                if self.spg_type_name not in duplicate_types
                else None
            )
            s_info = (s_name_zh or "") + (f"({s_desc})" if s_desc else "")

            p_name_zh, p_desc, _ = self.relation_info_en.get(_rel)
            p_desc = (p_desc or p_name_zh) if _rel not in duplicate_predicates else None
            p_info = (p_name_zh or "") + (f"({p_desc})" if p_desc else "")

            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            o_desc = (o_desc or o_name_zh) if o_type not in duplicate_types else None
            o_info = (o_name_zh or "") + (f"({o_desc})" if o_desc else "")

            spo_infos.append({"subject_type":s_name_zh, "predicate":p_name_zh, "object_type":o_name_zh})
            duplicate_predicates.add(_rel)
            duplicate_types.update([self.spg_type_name, o_type])
            predicates.append(p_name_zh)
        
        self.schema_list = self.multischema_split_by_num(spo_infos)




class DeepKE_KGPrompt(AutoPrompt):
    template_zh: str = "你是一个图谱实体知识结构化专家。根据输入实体类型(entity type)的schema描述，从文本中抽取出相应的实体实例和其属性信息，不存在的属性不输出, 属性存在多值就返回列表，并输出为可解析的json格式。"
    template_en: str = "You are an expert in structured knowledge systems for graph entities. Based on the schema description of the input entity type, you extract the corresponding entity instances and their attribute information from the text. Attributes that do not exist should not be output. If an attribute has multiple values, a list should be returned. The results should be output in a parsable JSON format."

    def __init__(
        self,
        spg_type_name: SPGTypeName,
        property_names: List[PropertyName] = None,
        relation_names: List[Tuple[RelationName, SPGTypeName]] = None,
        custom_prompt: str = None,
        language: str = 'zh',
    ):
        super().__init__()
        self.split_num = 4
        self.spg_type_name = spg_type_name
        if custom_prompt:
            self.template = custom_prompt
        if language == "zh":
            self.template = self.template_zh
        else:
            self.template = self.template_en
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


    def build_prompt(self, variables: Dict[str, str]) -> List[str]:
        instructions = []
        for schema in self.schema_list:
            instructions.append(
                json.dumps({
                    "instruction": self.template,
                    "schema": schema,
                    "input": variables.get("input"),
                },ensure_ascii=False,)
            )
        return instructions

    def parse_response(self, response: str) -> List[SPGRecord]:
        if isinstance(response, list) and len(response) > 0:
            response = response[0]
        try:
            re_obj = json.loads(response)
        except json.decoder.JSONDecodeError:
            raise ValueError("DeepKE_KGPrompt response JSONDecodeError error.")
        if type(re_obj) != dict:
            raise ValueError("DeepKE_KGPrompt response type error.")
        

        spo_records = []
        for entity_type, values in re_obj.items():  # entity_type
            if type(entity_type) != str or type(values) != dict:
                print(f"{entity_type} != str or {values} != dict")
                continue
            for head_ent, attributes in values.items():   # entity, attributes
                if type(head_ent) != str or type(attributes) != dict:
                    print(f"{head_ent} != str or {attributes} != dict")
                    continue
                for attribute, tail_ent in attributes.items(): # key, value
                    if type(tail_ent) == list:
                        for iit in tail_ent:
                            spo_records.append((head_ent, attribute, iit))
                    elif type(tail_ent) == str:
                        spo_records.append((head_ent, attribute, tail_ent))

    
        subject_records = {}
        result = []
        for s, p_zh, o in spo_records: 
            if s not in subject_records:
                subject_records[s] = (
                    SPGRecord(spg_type_name=self.spg_type_name)
                    .upsert_property("id", s)
                    .upsert_property("name", s)
                )
            if p_zh in self.property_info_zh:
                p, _, o_type = self.property_info_zh[p_zh]
                o_list = re.split("[,，、;；]", o)
                result.extend(
                    [
                        SPGRecord(o_type)
                        .upsert_property("id", _o)
                        .upsert_property("name", _o)
                        for _o in o_list
                    ]
                )
                o = subject_records[s].get_property(p)
                o = ",".join([o] + o_list) if o else ",".join(o_list)
                subject_records[s].upsert_property(p, o)
            elif p_zh in self.relation_info_zh:
                p, _, o_type = self.relation_info_zh[p_zh]
                if "." not in o_type or "STD." in o_type:
                    continue
                o_list = re.split("[,，、;；]", o)
                result.extend(
                    [
                        SPGRecord(o_type)
                        .upsert_property("id", _o)
                        .upsert_property("name", _o)
                        for _o in o_list
                    ]
                )
                o = subject_records[s].get_relation(p, o_type, "")
                o = ",".join([o] + o_list) if o else ",".join(o_list)
                subject_records[s].upsert_relation(p, o_type, o)
            else:
                continue

        for subject_record in subject_records.values():
            result.append(subject_record)
        return result
    

    def multischema_split_by_num(self, schemas: List[Any]):
        negative_length = max(len(schemas) // self.split_num, 1) * self.split_num
        total_schemas = []
        for i in range(0, negative_length, self.split_num):
            total_schemas.append(schemas[i:i+self.split_num])

        remain_len = max(1, self.split_num // 2)
        tmp_schemas = schemas[negative_length:]
        if len(schemas) - negative_length >= remain_len and len(tmp_schemas) > 0:
            total_schemas.append(tmp_schemas)
        elif len(tmp_schemas) > 0:
            total_schemas[-1].extend(tmp_schemas)
        return total_schemas


    def _render(self):
        spo_infos = defaultdict(set)
        predicates = []
        duplicate_types = set()
        duplicate_predicates = set()
        for _prop in self.property_names:
            s_name_zh, s_desc = self.spg_type_schema_info_en.get(self.spg_type_name)
            s_desc = (
                (s_desc or s_name_zh)
                if self.spg_type_name not in duplicate_types
                else None
            )
            s_info = (s_name_zh or "") + (f"({s_desc})" if s_desc else "")
            
            p_name_zh, p_desc, o_type = self.property_info_en.get(_prop)
            p_desc = (
                (p_desc or p_name_zh) if _prop not in duplicate_predicates else None
            )
            p_info = (p_name_zh or "") + (f"({p_desc})" if p_desc else "")
            
            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            o_desc = (o_desc or o_name_zh) if o_type not in duplicate_types else None
            o_info = (o_name_zh or "") + (f"({o_desc})" if o_desc else "")
            
            spo_infos[s_name_zh].add(p_name_zh)
            duplicate_predicates.add(_prop)
            duplicate_types.update([self.spg_type_name, o_type])
            predicates.append(p_name_zh)

        for _rel, o_type in self.relation_names:
            s_name_zh, s_desc = self.spg_type_schema_info_en.get(self.spg_type_name)
            s_desc = (
                (s_desc or s_name_zh)
                if self.spg_type_name not in duplicate_types
                else None
            )
            s_info = (s_name_zh or "") + (f"({s_desc})" if s_desc else "")

            p_name_zh, p_desc, _ = self.relation_info_en.get(_rel)
            p_desc = (p_desc or p_name_zh) if _rel not in duplicate_predicates else None
            p_info = (p_name_zh or "") + (f"({p_desc})" if p_desc else "")

            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            o_desc = (o_desc or o_name_zh) if o_type not in duplicate_types else None
            o_info = (o_name_zh or "") + (f"({o_desc})" if o_desc else "")

            spo_infos[s_name_zh].add(p_name_zh)
            duplicate_predicates.add(_rel)
            duplicate_types.update([self.spg_type_name, o_type])
            predicates.append(p_name_zh)

        spo_list = []
        for s, ps in spo_infos.items():
            spo_list.append({'entiyt_type':s, 'attributes':list(ps)})
        self.schema_list = self.multischema_split_by_num(spo_list)



class DeepKE_EEPrompt(AutoPrompt):
    template_zh: str = "你是专门进行事件提取的专家。请从input中抽取出符合schema定义的事件，不存在的事件返回空列表，不存在的论元返回NAN，如果论元存在多值请返回列表。请按照JSON字符串的格式回答。"
    template_en: str = "You are an expert in event extraction. Please extract events from the input that conform to the schema definition. Return an empty list for events that do not exist, and return NAN for arguments that do not exist. If an argument has multiple values, please return a list. Respond in the format of a JSON string."


    def __init__(
        self,
        event_type_name: SPGTypeName,
        property_names: List[PropertyName] = None,
        relation_names: List[Tuple[RelationName, SPGTypeName]] = None,
        custom_prompt: str = None,
    ):
        super().__init__()

        self.spg_type_name = event_type_name
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
            "event_type_name": event_type_name,
            "property_names": property_names,
            "relation_names": relation_names,
            "custom_prompt": custom_prompt,
        }


    def build_prompt(self, variables: Dict[str, str]) -> str:
        instructions = []
        for schema in self.schema_list:
            instructions.append(
                json.dumps({
                    "instruction": self.template,
                    "schema": schema,
                    "input": variables.get("input"),
                },ensure_ascii=False,)
            )
        return instructions


    def parse_response(self, response: str) -> List[SPGRecord]:
        if isinstance(response, list) and len(response) > 0:
            response = response[0]
        re_obj = json.loads(response)
        if "event" not in re_obj.keys():
            raise ValueError("EEPrompt response format error.")
        subject_records = []
        object_records = []
        for event_item in re_obj.get("event", []):
            if any(k not in event_item for k in ["event_type", "arguments"]):
                continue
            event_type, arguments = event_item["event_type"], event_item["arguments"]

            uuid_4 = uuid.uuid4()
            subject_record = SPGRecord(
                spg_type_name=self.spg_type_name
            ).upsert_property("id", uuid_4.hex)
            for p_zh, o in arguments.items():
                if p_zh in self.property_info_zh:
                    p, _, o_type = self.property_info_zh[p_zh]
                    if not o:
                        continue
                    subject_record.upsert_property(p, o)
                    if "." in o_type and "STD." not in o_type:
                        object_records.append(
                            SPGRecord(o_type)
                            .upsert_property("id", o)
                            .upsert_property("name", o)
                        )
                elif p_zh in self.relation_info_zh:
                    p, _, o_type = self.relation_info_zh[p_zh]
                    if not o:
                        continue
                    subject_record.upsert_relation(p, o_type, o)
                    if "." in o_type or "STD." not in o_type:
                        object_records.append(
                            SPGRecord(o_type)
                            .upsert_property("id", o)
                            .upsert_property("name", o)
                        )
                else:
                    continue
            subject_records.append(subject_record)

        return subject_records + object_records


    def _render(self):
        schema = {}
        description = {}
        arguments = ["名称"]
        for _prop in self.property_names:
            p_name_zh, p_desc, o_type = self.property_info_en.get(_prop)
            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            p_desc = (
                f"类型是{o_name_zh}"
                + ("，" + o_desc if o_desc else "")
                + f"。{p_desc or p_name_zh}"
            )
            arguments.append(p_name_zh)
            description[p_name_zh] = p_desc or p_name_zh
        for _rel, o_type in self.relation_names:
            p_name_zh, p_desc, _ = self.relation_info_en.get(_rel)
            o_name_zh, o_desc = self.spg_type_schema_info_en.get(o_type)
            name_zh = p_name_zh + "#" + o_name_zh
            desc = (
                f"类型是{o_name_zh}"
                + ("，" + o_desc if o_desc else "")
                + f"。{p_desc or p_name_zh}"
            )
            arguments.append(p_name_zh)
            description[name_zh] = desc

        s_name_zh, s_desc = self.spg_type_schema_info_en.get(self.spg_type_name)
        schema["arguments"] = arguments
        schema["event_type"] = s_name_zh
        description[s_name_zh] = s_desc or s_name_zh
        self.template = self.template.replace(
            "${schema}", json.dumps(schema, ensure_ascii=False)
        ).replace("${description}", json.dumps(description, ensure_ascii=False))
