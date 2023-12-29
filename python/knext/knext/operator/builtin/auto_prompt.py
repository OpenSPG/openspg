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
from abc import ABC
from typing import Union, List, Dict

from knext.client.model.base import BaseSpgType
from knext.client.schema import SchemaClient
from knext.common.schema_helper import SPGTypeHelper, PropertyHelper
from knext.operator.op import PromptOp
from knext.operator.spg_record import SPGRecord


class AutoPrompt(PromptOp, ABC):
    pass


class REPrompt(AutoPrompt):

    template: str = """
已知SPO关系包括:${schema}
从下列句子中提取定义的这些关系。最终抽取结果以json格式输出。
input:${input}
输出格式为:{"spo":[{"subject":,"predicate":,"object":},]}
"output":
    """

    def __init__(
        self,
        spg_type_name: Union[str, SPGTypeHelper],
        property_names: List[Union[str, PropertyHelper]],
        custom_prompt: str = None,
    ):
        super().__init__()

        if custom_prompt:
            self.template = custom_prompt
        self.schema_client = SchemaClient()
        spg_type = self.schema_client.query_spg_type(spg_type_name=spg_type_name)
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
        if isinstance(response, list) and len(response) > 0:
            response = response[0]
        re_obj = json.loads(response)
        if "spo" not in re_obj.keys():
            raise ValueError("SPO format error.")
        subject_properties = {}
        for spo_item in re_obj.get("spo", []):
            if spo_item["predicate"] not in self.predicate_zh_to_en_name:
                continue
            subject_properties = {
                "id": spo_item["subject"],
                "name": spo_item["subject"],
            }
            if spo_item["subject"] not in subject:
                subject[spo_item["subject"]] = subject_properties
            else:
                subject_properties = subject[spo_item["subject"]]

            spo_en_name = self.predicate_zh_to_en_name[spo_item["predicate"]]

            import re

            spo_item["object"] = ",".join(re.split("[,，、;；]", spo_item["object"]))
            if spo_en_name in subject_properties and len(
                subject_properties[spo_en_name]
            ):
                subject_properties[spo_en_name] = (
                    subject_properties[spo_en_name] + "," + spo_item["object"]
                )
            else:
                subject_properties[spo_en_name] = spo_item["object"]

        subject_entity = SPGRecord(
            spg_type_name=self.spg_type_name, properties=subject_properties
        )
        result.append(subject_entity)
        return result

    def _render(self, spg_type: BaseSpgType, property_names: List[str]):
        spos = []
        repeat_desc = []
        for property_name in property_names:
            prop = spg_type.properties.get(property_name)
            object_desc = ""
            object_type = self.schema_client.query_spg_type(prop.object_type_name)
            if object_type:
                object_desc = object_type.desc
            spos.append(
                f"{spg_type.name_zh}"
                + (
                    f"({spg_type.desc or spg_type.name_zh})"
                    if spg_type.name_zh not in repeat_desc
                    else ""
                )
                + f"-{prop.name_zh}"
                + (
                    f"({prop.desc or prop.name_zh})"
                    if prop.name_zh not in repeat_desc
                    else ""
                )
                + f"-{prop.object_type_name_zh}"
                + (
                    f"({object_desc or prop.object_type_name_zh})"
                    if prop.object_type_name_zh not in repeat_desc
                    else ""
                )
            )
            repeat_desc.extend(
                [spg_type.name_zh, prop.name_zh, prop.object_type_name_zh]
            )
        schema_text = "\n[" + ",\n".join(spos) + "]"
        self.template = self.template.replace("${schema}", schema_text)
