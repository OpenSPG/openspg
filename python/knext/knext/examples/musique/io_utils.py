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
import os
import numpy as np
import pandas as pd
from tqdm import tqdm
from knext.ca.common.utils import logger


class MusiqueDataset(object):
    def __init__(self, data_dir, version="ans", tag="test", debug_mode=False):
        self.data_dir = data_dir
        self.debug_mode = debug_mode
        self.version = version
        self.tag = tag

        data_file = self.get_data_file(version, tag)
        self.question_paragraphs_dict = {}
        self.question_answer_dict = {}
        self.question_id_dict = {}
        self.quesion_list = []
        self.question_line_dict = {}

        with open(data_file, "r") as f:
            for line_data in f:
                line = json.loads(line_data)
                question = line["question"]
                self.question_line_dict[question] = line
                id = line["id"]
                self.quesion_list.append(question)
                # question answer
                # question id
                self.question_id_dict[question] = id
                if "answer" in line:
                    answer = line["answer"]
                    self.question_answer_dict[question] = answer
                # question paragraphs

                idx_title_para_list = []

                for paragraph in line["paragraphs"]:
                    idx_title_para_dict = {}
                    paragraph_idx = paragraph["idx"]
                    paragraph_text = paragraph["paragraph_text"]
                    paragraph_title = paragraph["title"]
                    idx_title_para_dict["idx"] = paragraph_idx
                    idx_title_para_dict["title"] = paragraph_title
                    idx_title_para_dict["paragraph_text"] = paragraph_text
                    idx_title_para_list.append(idx_title_para_dict)

                self.question_paragraphs_dict[question] = idx_title_para_list

    def get_all_questions(self):
        return self.quesion_list

    def get_data_line_by_question(self, question):
        return self.question_line_dict[question]

    def convert_question_to_hash(self, question):
        import re
        import hashlib

        name = question
        name = name.strip()
        rep_name = re.sub(r"[^a-zA-Z0-9]", "_", name)
        md5_hash = hashlib.md5()
        md5_hash.update(name.encode("utf-8"))
        md5_value = md5_hash.hexdigest()
        return f"{rep_name[:128]}.{md5_value}"

    def get_para_list_by_question(self, question):
        return [d["paragraph_text"] for d in self.question_paragraphs_dict[question]]

    def get_para_idx_list_by_question(self, question):
        return self.question_paragraphs_dict[question]

    def get_answer_by_question(self, question):
        if question in self.question_answer_dict:
            return self.question_answer_dict[question]
        else:
            return None

    def get_id_by_question(self, question):
        return self.question_id_dict[question]

    def get_data_file(self, version, tag):
        return os.path.join(self.data_dir, f"musique_{version}_v1.0_{tag}.jsonl")


def generate_submission_file(output_file_path, musique_dataset, agent_results):
    question_agent_result_dict = {}
    for agent_result in agent_results:
        question = agent_result["question"]
        question_agent_result_dict[question] = agent_result

    logger.info(f"generate_submission_file. agent_results len: {len(agent_results)}")
    with open(output_file_path, "w") as f:
        for question in tqdm(
            musique_dataset.get_all_questions(), desc="Writing Into Submission File"
        ):
            line_dict = {}
            line_dict["id"] = musique_dataset.get_id_by_question(question)
            line_dict["predicted_answer"] = question_agent_result_dict[question][
                "predicted_answer"
            ]
            line_dict["predicted_support_idxs"] = question_agent_result_dict[question][
                "predicted_support_idxs"
            ]
            line_dict["predicted_answerable"] = True
            line_str = json.dumps(line_dict)
            f.write(line_str + "\n")
