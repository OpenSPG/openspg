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

from typing import Dict, List

from knext.operator.op import ExtractOp
from knext.operator.spg_record import SPGRecord

prompt_template = {
    "ner": '已知实体类型(entity_type)包括:${schema}。假设你是一个专业的医学专家，请从下列文本中抽取所有实体(entity)。\n----文本----\n${input}\n----回答要求----\n1. 答案格式为：[{"entity": ,"entity_type": },]',
    "re": '假设你是一个专业的医学专家，请从文本中抽取关系。我们会首先提供文本，然后会提供知识图谱schema，再提供回答的具体要求，最后是一个举例。\n----文本----\n${input}\n----知识图谱schema----\n${schema}\n----回答要求----\n1. 答案格式为json格式：[{"subject":,"predicate":,"object":},]\n2. object要求简洁，必须是中文，如果object包含多个值请用英文逗号分隔；\n3. 每一条关系必须属于知识图谱schema。\n----举例----\n文本为：急性扁桃体炎通常伴有咽痛，声嘶，发热等症状。回答为：{"subject":"急性扁桃体炎","predicate":"症状","object":"咽痛,声嘶,发热"}',
    "convert": "请把上面的回答转成标准的json格式，并只返回json数据作为回答，不需要其他说明",
}


class DiseaseExtractor(ExtractOp):
    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def eval(self, record: Dict[str, str]) -> List[SPGRecord]:
        print(record)
        print(self.params)
        return [SPGRecord("Medical.Disease", {"id": "123", "name": "123"})]
