import os
import json
import asyncio
from concurrent.futures import ThreadPoolExecutor, as_completed
from antcode.openspgapp.openspg.python.knext.knext.ca.common.base import Question, KagBaseModule, Agent
from knext.ca.logic.agents.divide_and_conquer import DivideAndConquerAgent
from knext.ca.logic.modules.reasoner import AnswerQuestionWithContext
from antcode.openspgapp.openspg.python.knext.knext.ca.tools.info_processor import LoggerIntermediateProcessTool
from knext.ca.common.utils import logger


class MusiqueAnswerQuestionWithSPO(AnswerQuestionWithContext):
    def __init__(self, llm_module, spo_info_tool, prompt_template_dir=None):
        spo_info_tool = spo_info_tool
        super().__init__(llm_module, False, prompt_template_dir)
        self.spo_info_tool = spo_info_tool

    def get_module_name(self):
        return "MusiqueAnswerQuestionWithSPO" 

    def get_template_var_names(self):
        return ['question', 'context']
    
    def get_extra_info_fetch_tools(self):
        return [self.spo_info_tool]

    def preprocess(self, question: Question):
        context = self.spo_info_tool.fetch_info(question.question)        
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            context=context,
        )
        logger.info(f'\nMusiqueAnswerQuestionWithSPO\nquestion:{question.question}\ncontext:\n{context}')
        return prompt

    def postprocess(self, question: Question, llm_output):
        info_dict = {
            'status': 'MusiqueAnswerQuestionWithSPO postprocess',
            'log_info': f'question: {question.question}\nllm_output: {llm_output}'
        }
        self.process_intermediate_info(info_dict)
        try:
            parts = llm_output.split('.')
            answer = parts[0].split(':')[-1].strip()
            para_idx = parts[1].split(':')[-1].strip()
            if para_idx.isdigit():
                self.spo_info_tool.store_supported_idx([int(para_idx)])
            return answer
        except Exception as err:
            logger.warning(f'MusiqueAnswerQuestionWithSPO postprocess fail with err: {err}.\n  question: {question.question}\n  llm_output: {llm_output}\n')
            return 'not_found'


class MusiqueAnswerQuestionWithText(AnswerQuestionWithContext):
    def __init__(self, llm_module, text_info_tool, prompt_template_dir=None):
        super().__init__(llm_module, False, prompt_template_dir)
        self.text_info_tool = text_info_tool

    def get_module_name(self):
        return "MusiqueAnswerQuestionWithText" 

    def get_template_var_names(self):
        return ['question', 'context']

    def get_extra_info_fetch_tools(self):
        return [self.text_info_tool]

    def preprocess(self, question: Question):
        text_list = self.text_info_tool.fetch_info(question.question)
        context = ""
        for idx, text in text_list:
            context += f"text: {text}; para_idx: {idx}\n"
            
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            context=context,
        )
        logger.info(f'\nMusiqueAnswerQuestionWithSPO\nquestion:{question.question}\ncontext: {context}')
        return prompt

    def postprocess(self, question: Question, llm_output):
        info_dict = {
            'status': 'MusiqueAnswerQuestionWithSPO postprocess',
            'log_info': f'question: {question.question}\nllm_output: {llm_output}'
        }
        self.process_intermediate_info(info_dict)
        try:
            parts = llm_output.split('.')
            answer = parts[0].split(':')[-1].strip()
            para_idx = parts[1].split(':')[-1].strip()
            if para_idx.isdigit():
                self.text_info_tool.store_supported_idx([int(para_idx)])
            return answer
        except Exception as err:
            logger.warning(f'MusiqueAnswerQuestionWithSPO postprocess fail with err: {err}.\n  question: {question.question}\n  llm_output: {llm_output}\n')
            return 'not found'


class HierarchicalAnswerQuestion(KagBaseModule):
    def __init__(self, llm_module, answer_with_spo, answer_with_text):
        super().__init__(llm_module, is_computational=False)
        self.answer_with_spo = answer_with_spo
        self.answer_with_text = answer_with_text
            
    def forward(self, question: Question):
        spo_result = self.answer_with_spo.forward(question)
        logger.info(f'\nHierarchicalAnswerQuestion after answer_with_spo.\n  question:{question.question}\n  spo_result: {spo_result}')
        if spo_result == 'not_found':
            logger.info(f'answer_with_spo not found result for question {question.question}, prepare to call answer_with_text')
            text_result = self.answer_with_text.forward(question)
            return text_result
        else:
            return spo_result 

    def get_extra_info_fetch_tools(self):
        info_tools = []
        info_tools.extend(self.answer_with_spo.get_extra_info_fetch_tools())
        info_tools.extend(self.answer_with_text.get_extra_info_fetch_tools())
        return info_tools


class MusiqueDivideAndConquerAgent(DivideAndConquerAgent):
    def __init__(self, 
            llm,
            prompt_template_dir,
            answer_parent_question,
            answer_atom_question,
            debug_mode,
            max_depth=1,
        ):
        use_default_prompt_template = False
        intermediate_process_tools = []
        intermediate_process_tools.append(
            LoggerIntermediateProcessTool(debug_mode=debug_mode)
        )

        super().__init__(
            llm=llm,
            intermediate_process_tools=intermediate_process_tools,
            max_depth=max_depth,
            answer_parent_question=answer_parent_question,
            answer_atom_question=answer_atom_question,
            use_default_prompt_template=use_default_prompt_template,
            prompt_template_dir=prompt_template_dir,
        )

    def solve_problem(self, question: Question):
        for extra_info_tool in self.extra_info_fetch_tools:
            extra_info_tool.prepare_for_question(question.question)
            
        answer = super().solve_problem(question)
        
        predicted_support_idxs = []
        for extra_info_tool in self.extra_info_fetch_tools:
            predicted_support_idxs.extend(extra_info_tool.fetch_supported_idx_by_question(question.question))
        
        result = {
            'question': question.question,
            'predicted_answer': answer,
            'predicted_support_idxs': predicted_support_idxs,
        }
        return result









