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

import argparse
import json

from knext.core.schema import Schema


def get_schema(spg_type_name):
    schema = Schema()
    spg_type = schema.query_spg_type(spg_type_name)
    return spg_type


def get_re_prompt(template_path, spg_type) -> str:
    """
    生成RE任务的Prompt
    """
    schema_text = ""
    for k, v in spg_type.properties.items():
        if v.name in ["id", "description"]:
            continue
        spo = '"subject":"{}","predicate":"{}","object":"{}"'.format(
            spg_type.name_zh, v.name_zh, v.object_type_name_zh
        )
        spo = "{" + spo + "}\n"
        schema_text = schema_text + spo

    f = open(template_path, "r")
    prompt_template = json.load(f)
    prompt = prompt_template["re"].replace("${schema}", schema_text)
    return prompt


def get_ner_prompt(template_path, spg_type) -> str:
    """
    生成NER任务的Prompt
    """
    f = open(template_path, "r")
    prompt_template = json.load(f)
    prompt = prompt_template["ner"].replace(
        "${schema}", f"[{spg_type.name}:{spg_type.name_zh}]"
    )
    return prompt


def process(
    src_path: str, tgt_path: str, entity_type: str, template_path: str, task_type: str
):
    spg_type = get_schema(entity_type)

    writer = open(tgt_path, "w", encoding="utf-8")
    with open(src_path, "r", encoding="utf-8") as reader:
        for line in reader:
            print(line)
            record = json.loads(line)
            if task_type == "RE":
                prompt_template = get_re_prompt(template_path, spg_type)
                instruct = prompt_template.replace("${input}", record["input"])
            elif task_type == "NER":
                prompt_template = get_ner_prompt(template_path, spg_type)
                instruct = prompt_template.replace("${input}", record["input"])
            else:
                raise KeyError

            record = {"content": instruct, "summary": record["output"]}
            writer.write(json.dumps(record, ensure_ascii=False) + "\n")


if __name__ == "__main__":
    """

    python convert_util.py \
        --entity_type Medical.Disease \
        --task_type RE \
        --src_path RE/sample.json \
        --tgt_path RE/processed.json \
        --template_path ../../../schema/prompt.json

    """

    parse = argparse.ArgumentParser()
    parse.add_argument("--entity_type", type=str)
    parse.add_argument("--task_type", type=str, choices=["RE", "NER"])
    parse.add_argument("--src_path", type=str, default="NER/sample.json")
    parse.add_argument("--tgt_path", type=str, default="NER/processed.json")
    parse.add_argument(
        "--template_path", type=str, default="../../../schema/prompt.json"
    )

    options = parse.parse_args()
    options = vars(options)
    process(**options)
