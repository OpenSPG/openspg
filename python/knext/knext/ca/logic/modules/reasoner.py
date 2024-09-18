import os
import re
import asyncio
from string import Template
from collections import deque
import json
from knext.ca.common.base import Question, KagBaseModule
from knext.ca.common.utils import logger


class Reasoner(KagBaseModule):
    def __init__(self, llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn):
        super().__init__(
            llm_module=llm_module,
            use_default_prompt_template=use_default_prompt_template,
            prompt_template_dir=prompt_template_dir,
            is_prompt_template_cn=is_prompt_template_cn
        )


class IsAtomQuestion(Reasoner):
    """
    Module for determining if a question pertains to atomic concepts based on the input question.

    """

    def __init__(self, llm, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn=False):
        use_default_prompt_template = False
        super().__init__(llm, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

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
        if self.is_prompt_template_cn:
            yes_token = '是'
            no_token = '否'
        else:
            yes_token = 'YES'
            no_token = 'NO'

        if llm_output == yes_token:
            return True
        elif llm_output == no_token:
            return False
        else:
            warning_reuslt = f'result: {llm_output}'
            logger.warning(f'{warning_reuslt}')
            return True


class DoesQuestionNeedExtraInfo(Reasoner):
    """
    Module for determining if a question needs additional information based on the question.

    """

    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None,
                 is_prompt_template_cn=False):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

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
        if self.is_prompt_template_cn:
            yes_token = '是'
            no_token = '否'
        else:
            yes_token = 'YES'
            no_token = 'NO'

        if llm_output == yes_token:
            return True
        elif llm_output == no_token:
            return False
        else:
            warning_reuslt = f'result: {llm_output}'
            logger.debug(f'{warning_reuslt}')
            return False


class ExtractTriplesFromTextModule(Reasoner):
    """
    Module to extract valid infomation into triples from a text.

    """

    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None,
                 is_prompt_template_cn=False):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

    def get_module_name(self):
        return "ExtractTriplesFromTextModule"

    def get_template_var_names(self):
        return ['text']

    def forward(self, text):
        TEMPERATURE = 0.0
        prompt = self.state_dict['prompt_template'].substitute(
            text=text,
        )
        llm_output = self.llm_module.generate(prompt, temperature=TEMPERATURE)
        triples_list = json.loads(llm_output.replace('```json', '').replace('```', '').strip())
        return triples_list


class FetchSubject(Reasoner):
    """
    Module for determining the subject of a query.

    """

    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None,
                 is_prompt_template_cn=False):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            answer=question.answer
        )
        return prompt

    def get_module_name(self):
        return "FetchSubject"

    def get_template_var_names(self):
        return ['question', 'answer']


class FetchPredicate(Reasoner):
    """
    Module for determining the predicate of a query.

    """

    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None,
                 is_prompt_template_cn=False):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            answer=question.answer
        )
        return prompt

    def get_module_name(self):
        return "FetchPredicate"

    def get_template_var_names(self):
        return ['question', 'answer']


class FetchObject(Reasoner):
    """
    Module for determining the object of a query.

    """

    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None,
                 is_prompt_template_cn=False):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            answer=question.answer
        )
        return prompt

    def get_module_name(self):
        return "FetchObject"

    def get_template_var_names(self):
        return ['question', 'answer']




