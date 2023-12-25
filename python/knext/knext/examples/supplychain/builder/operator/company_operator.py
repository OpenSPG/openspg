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

from typing import List

import requests

from knext.api.record import SPGRecord
from knext.api.operator import LinkOp
from knext.client.search import SearchClient


def llm_infer(word, recall):
    """
    Here is the implement of LLM inferring
    """

    prompt_text = f"你作为一个语言专家，请在目标词里选出跟输入词意思最相近的一个词，如果没有意思相近的则输出null。\n要求：输出结果直接显示选中的目标词，不需要给出选择的任何理由。\n输入词：{word}。\n目标词：[{recall}]。"
    param = {"prompt": prompt_text, "history": None}
    llm_response = requests.post("http://127.0.0.1:8888", json=param)
    if llm_response.status_code == 200:
        content = llm_response.content
        if content.startswith("输出结果:"):
            return content[content.index(":") + 1 :].strip().rstrip("。")
    else:
        return "null"


class CompanyLinkerOperator(LinkOp):
    bind_to = "SupplyChain.Company"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("SupplyChain.Company")
        self.enable_llm = False

    def eval(self, property: str, record: SPGRecord) -> List[SPGRecord]:
        company_name = property
        query = {"match": {"name": company_name}}
        recalls = self.search_client.search(query, start=0, size=30)

        if not recalls:
            return []

        if recalls[0].score < 0.6:
            # Low similarity, discard recall results
            return []

        if company_name == recalls[0].properties["name"]:
            # If the result of Top1 is the same as the attribute value, then returned directly
            return [SPGRecord(spg_type_name="SupplyChain.Company", properties={"id": recalls[0].doc_id})]

            # Perform fine-ranking on coarse recall results by calling LLM
        if not self.enable_llm:
            return [SPGRecord(spg_type_name="SupplyChain.Company", properties={"id": recalls[0].doc_id})]
        recall_dict = {}
        for item in recalls:
            recall_dict[item.properties["name"]] = item.doc_id
        recall_str = ",".join(recall_dict.keys())

        # ----- Please enable the code below when LLM service is ready ------
        llm_result = llm_infer(company_name, recall_str)
        if len(llm_result) > 0 and llm_result != "null":
            return [
                SPGRecord(
                    spg_type_name="SupplyChain.Company",
                    properties={"id": recall_dict[llm_result]}
                )
            ]
        return []
