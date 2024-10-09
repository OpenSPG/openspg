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

import os
import re
import asyncio
from string import Template
from collections import deque
import json
from knext.ca.common.base import Question, KagBaseModule
from knext.ca.common.utils import logger


class Planner(KagBaseModule):
    def __init__(self, llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn):
        super().__init__(
            llm_module=llm_module,
            use_default_prompt_template=use_default_prompt_template,
            is_prompt_template_cn=is_prompt_template_cn,
            prompt_template_dir=prompt_template_dir
        )


class DivideQuestion(Planner):
    """
    Module for dividing a question into serveral sub questions.

    """

    def __init__(self, llm_module, use_default_prompt_template=False, prompt_template_dir=None,
                 is_prompt_template_cn=True):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

    def get_module_name(self):
        return "DivideQuestion"

    def get_template_var_names(self):
        return ['question']

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question
        )
        self.question = question
        return prompt

    def postprocess(self, question: Question, llm_output):
        def _output_parse(_output_string):
            # parse output
            parts = _output_string.split("llm_output:")
            result = parts[-1] if len(parts) > 1 else _output_string
            # parse \n
            if self.is_prompt_template_cn:
                parts_2 = result.split('依赖关系是:')
            else:
                parts_2 = result.split('dependent relationship:')
            qustion = parts_2[0].strip().split('\n')
            dep = parts_2[1].strip().split('\n')
            return qustion, dep

        def _process_dep(_input_list):
            dep_dict = {}
            for dep in _input_list:
                if self.is_prompt_template_cn:
                    res = dep.split("依赖")
                else:
                    res = dep.split("deps")
                assert len(res) == 2
                key = res[0].strip()
                dep = res[1].strip('"').split(",") if "," in res[1] else res[1].strip().split("，")
                dep_real = [dep_i.strip() for dep_i in dep]
                dep_dict[key] = dep_real
            return dep_dict

        result_string = _output_parse(llm_output)
        sub_questions_list = []
        for org_question in result_string[0]:
            sub_questions_list.append(org_question)
        sub_dependencies = _process_dep(result_string[1])
        qk_Q_map = {}

        org_question_children = []
        for q_ind, sub_question in enumerate(sub_questions_list):
            if self.is_prompt_template_cn:
                q_key = f'问题{q_ind + 1}'
            else:
                q_key = f'question{q_ind + 1}'
            q_deps_Q_list = []
            for q_dep in sub_dependencies[q_key]:
                if q_dep == "None":
                    pass
                else:
                    q_deps_Q_list.append(qk_Q_map[q_dep])
            qk_Q_map[q_key] = Question(sub_question, q_deps_Q_list, [])
            qk_Q_map[q_key].parent = question
            org_question_children.append(qk_Q_map[q_key])
        self.question.children = org_question_children
        return list(qk_Q_map.values())


class RewriteQuestionBasedOnDeps(Planner):
    """
    Module for rewriting a question based on the current question and dependent question

    """

    def __init__(self, llm_module, use_default_prompt_template=False, prompt_template_dir=None,
                 is_prompt_template_cn=True):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

    def get_module_name(self):
        return "RewriteQuestionBasedOnDeps"

    def get_template_var_names(self):
        return ['question', 'context']

    def preprocess(self, question: Question):
        context_string = ''
        if len(question.dependencies) > 0:
            if self.is_prompt_template_cn:
                for q in question.dependencies:
                    context_string += f"问题: {q.question} \n 答案: {q.answer}" + '\n'
            else:
                for q in question.dependencies:
                    context_string += f"question: {q.question} \n answer: {q.answer}" + '\n'
            prompt = self.state_dict['prompt_template'].substitute(
                question=question.question,
                context=context_string
            )
            return prompt
        else:
            return None

    def postprocess(self, question: Question, llm_output):
        parts = llm_output.split("llm_output:")
        result = parts[-1] if len(parts) > 1 else llm_output
        return result


