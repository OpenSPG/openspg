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


from nn4k.invoker import NNInvoker

from knext.builder.component import (
    CSVReader,
    LLMBasedExtractor,
    SPGTypeMapping,
    KGWriter,
)
from knext.builder.operator import DeepKE_KGPrompt
from knext.builder.model.builder_job import BuilderJob


from schema.deepke_schema_helper import DeepKE



class Disease(BuilderJob):
    def build(self):

        source = CSVReader(
            local_path="builder/job/data/Disease.csv",
            columns=["input"],
            start_row=1,
        )

        extract = LLMBasedExtractor(
            llm=NNInvoker.from_config("builder/model/local_infer.json"),
            prompt_ops=[
                DeepKE_KGPrompt(
                    spg_type_name=DeepKE.Disease,
                    property_names=[
                        DeepKE.Disease.complication,
                        DeepKE.Disease.commonSymptom,
                        DeepKE.Disease.applicableDrug,
                        DeepKE.Disease.department,
                        DeepKE.Disease.diseaseSite,
                    ],
                    relation_names=[(DeepKE.Disease.abnormal, DeepKE.Indicator)],
                )
            ],
        )

        mappings = [
            SPGTypeMapping(spg_type_name=DeepKE.Disease),
            SPGTypeMapping(spg_type_name=DeepKE.BodyPart),
            SPGTypeMapping(spg_type_name=DeepKE.Drug),
            SPGTypeMapping(spg_type_name=DeepKE.HospitalDepartment),
            SPGTypeMapping(spg_type_name=DeepKE.Symptom),
            SPGTypeMapping(spg_type_name=DeepKE.Indicator),
        ]

        sink = KGWriter()

        return source >> extract >> mappings >> sink



'''
def main():
    # example for local inference
    invoker = NNInvoker.from_config("local_infer.json5")
    task = 'RE'
    language = 'zh'
    schemas = ['身高', '高管', '简称', '毕业院校']

    event_schemas =  [
        {
            'event_type': '热点事件'
            'trigger': True,
            'arguments': ['主体', '时间', '地点', '事件描述', '客体', '行为', '行为对象']
        }
    ]
    ner_schemas = ['人物', '组织', '地点', '时间', '数字', '其他']

    extract_func = get_extract_func(task)
    instructions = []
    with open('data/RE/sample.json', "r") as reader:
        for line in reader:
            data = json.loads(line)
            instructions.extend(get_instruction(language, task, schemas, data['text']))

    for instruction in instructions:
        sinstr = '<reserved_106>' + instruction + '<reserved_107>'
        answer = invoker.local_inference(
            sinstr,
            tokenize_config={"padding": True},
            delete_heading_new_lines=True,
        )[0]
        print(answer)
        flag, kgs = extract_func(answer)
        print(flag, kgs)
'''

if __name__ == "__main__":
    main()
