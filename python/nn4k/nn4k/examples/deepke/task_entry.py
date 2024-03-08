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

from nn4k.invoker.base import NNInvoker
import json
from ie2instruction.convert_func import get_test_data
from ie2instruction.convert.processer import get_processer
from ie2instruction.eval.extracter import get_extracter


def main():
    # example for local inference
    invoker = NNInvoker.from_config("local_infer.json5")
    task = 'NER'
    language = 'zh'
    split_num = 6

    #for task, split_num in [['EET', 4], ['EEA', 4], ['KG', 4]]:
    processer_class = get_processer(task)  
    processer = processer_class.read_from_file(    
        processer_class, 'data/NER/schema.json', negative=-1
        #processer_class, f'data/{task}/schema.json', negative=-1
    )
    extracter = get_extracter(task)()

    records = []
    with open('data/NER/sample.json', "r") as reader:
    #with open(f'data/{task}/sample.json', "r") as reader:
        for line in reader:
            data = json.loads(line)
            records.append(data)
    instructions = get_test_data(task, language, 'all', records, processer, split_num)

    for instruction in instructions:
        sinstr = '<reserved_106>' + instruction['instruction'] + '<reserved_107>'
        answer = invoker.local_inference(
            sinstr,
            tokenize_config={"padding": True},
            delete_heading_new_lines=True,
        )[0]
        print(answer)
        flag, kgs = extracter.extract(answer)
        print(flag, kgs)


if __name__ == "__main__":
    main()
