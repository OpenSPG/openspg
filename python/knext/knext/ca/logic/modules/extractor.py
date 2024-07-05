import os
import re
import asyncio
from string import Template
from collections import deque
import json
from knext.ca.common.base import Question, KagBaseModule
from knext.ca.common.utils import logger


class ExtractTriplesFromTextModule(KagBaseModule):
    """
    Module to extract valid infomation into triples from a text.

    """
    def __init__(self, llm_module, prompt_template_dir=None):
        super().__init__(llm_module, False, prompt_template_dir)

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
        logger.info(f'\nExtractTriplesFromTextModule\n  text: {text}\n  llm_output: {llm_output}\n  ')
        triples_list = json.loads(llm_output.replace('```json', '').replace('```', '').strip())
        return triples_list
    
    
class FetchSubject(KagBaseModule):
    """
    Module for determining the subject of a query.

    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir)

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            answer=question.answer
        )
        #logger.info(f'FetchSubject prompt:\n{prompt}')
        return prompt 

    def get_module_name(self):
        return "FetchSubject"

    def get_template_var_names(self):
        return ['question', 'answer']


class FetchPredicate(KagBaseModule):
    """
    Module for determining the predicate of a query.

    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir)

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            answer=question.answer
        )
        #logger.info(f'FetchPredicate prompt:\n{prompt}')
        return prompt 

    def get_module_name(self):
        return "FetchPredicate"

    def get_template_var_names(self):
        return ['question', 'answer']
    

class FetchObject(KagBaseModule):
    """
    Module for determining the object of a query.

    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir)

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            answer=question.answer
        )
        #logger.info(f'FetchObject prompt:\n{prompt}')
        return prompt 

    def get_module_name(self):
        return "FetchObject"

    def get_template_var_names(self):
        return ['question', 'answer']

