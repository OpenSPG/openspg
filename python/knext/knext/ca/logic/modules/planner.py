import os
import re
import asyncio
from string import Template
from collections import deque
import json
from knext.ca.common.base import Question, KagBaseModule
from knext.ca.common.utils import logger


class DivideQuestion(KagBaseModule):
    """
    Module for dividing a question into serveral sub questions.
    
    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir)

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
            parts = _output_string.split("llm_output:")  # 分割一次
            result = parts[-1] if len(parts) > 1 else _output_string
            # parse \n
            parts_2 = result.split('依赖关系是:')
            qustion = parts_2[0].strip().split('\n')
            dep = parts_2[1].strip().split('\n')
            return qustion, dep

        def _process_dep(_input_list):
            dep_dict = {}
            for dep in _input_list:
                res = dep.split("依赖")
                assert len(res) == 2
                key = res[0].strip()
                dep = res[1].strip('"').split(",") if "," in res[1] else res[1].strip().split("，")
                dep_real = [dep_i.strip() for dep_i in dep]
                dep_dict[key] = dep_real
            return dep_dict
        result_string = _output_parse(llm_output)
        sub_questions_list = []
        sub_logic_forms_list = []
        for org_question in result_string[0]:
            sub_questions_list.append(org_question)
        sub_dependencies = _process_dep(result_string[1])
        qk_Q_map = {}

        org_question_children = []
        for q_ind, sub_question in enumerate(sub_questions_list):
            q_key = f'问题{q_ind+1}'
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


class RewriteQuestionBasedOnDeps(KagBaseModule):
    """
    Module for rewriting a question based on the current question and dependent question

    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir)

    def get_module_name(self):
        return "RewriteQuestionBasedOnDeps"

    def get_template_var_names(self):
        return ['question', 'context']

    def preprocess(self, question: Question):
        context_string = ''    
        if len(question.dependencies) > 0:
            for q in question.dependencies:
                context_string += f"问题: {q.question} \n 答案: {q.answer}"+'\n'
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


class IsAtomQuestion(KagBaseModule):
    """
    Module for determining if a question pertains to atomic concepts based on the input question.

    """
    def __init__(self, llm, use_default_prompt_template, prompt_template_dir):
        use_default_prompt_template = False
        super().__init__(llm, use_default_prompt_template, prompt_template_dir)

    def get_module_name(self):
        return "IsAtomQuestion"

    def get_template_var_names(self):
        return ['question']

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
        )
        return prompt 

    def postprocess(self, question: Question, llm_output):
        llm_output = llm_output.split(':')[-1].strip()
        if llm_output == '是':
            return True
        elif llm_output == '否':
            return False
        else:
            warning_reuslt = f'结果为:{llm_output}'
            logger.warning(f'{warning_reuslt}')
            return True


class DoesQuestionNeedExtraInfo(KagBaseModule):
    """
    Module for determining if a question needs additional information based on the question.

    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir)

    def get_module_name(self):
        return "DoesQuestionNeedExtraInfo"

    def get_template_var_names(self):
        return ['question']

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question
        )
        return prompt

    def postprocess(self, question: Question, llm_output):
        if llm_output == '是':
            return True
        elif llm_output == '否':
            return False
        else:
            warning_reuslt = f'结果为:{llm_output}'
            logger.debug(f'{warning_reuslt}')
            return False
