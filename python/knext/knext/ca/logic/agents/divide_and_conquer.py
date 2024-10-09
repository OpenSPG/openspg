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

import asyncio

from knext.ca.common.base import Question, Agent
from knext.ca.logic.modules.solver import SolveQuestionWithContext
from knext.ca.logic.modules.planner import (
    DivideQuestion,
    CheckDivideQuestion,
    RewriteQuestionBasedOnDeps,
)
from knext.ca.logic.modules.reasoner import IsAtomQuestion


class DivideAndConquerAgent(Agent):
    def __init__(
        self,
        llm,
        intermediate_process_tools,
        max_depth=1,
        divide_question=None,
        rewrite_question=None,
        is_atom_question=None,
        solve_parent_question=None,
        solve_atom_question=None,
        use_default_prompt_template=False,
        prompt_template_dir=None,
        use_en_log=True,
        check_divide_question=None,
        **kwargs,
    ):
        self.llm = llm
        self.max_depth = max_depth
        self.use_en_log = use_en_log

        self.divide_question = (
            divide_question
            if divide_question
            else DivideQuestion(
                self.llm, use_default_prompt_template, prompt_template_dir
            )
        )

        self.check_divide_question = (
            check_divide_question
            if check_divide_question
            else CheckDivideQuestion(
                self.llm, use_default_prompt_template, prompt_template_dir
            )
        )

        self.rewrite_question = (
            rewrite_question
            if rewrite_question
            else RewriteQuestionBasedOnDeps(
                self.llm, use_default_prompt_template, prompt_template_dir
            )
        )

        self.is_atom_question = (
            is_atom_question
            if is_atom_question
            else IsAtomQuestion(
                self.llm, use_default_prompt_template, prompt_template_dir
            )
        )

        self.solve_parent_question = (
            solve_parent_question
            if solve_parent_question
            else SolveQuestionWithContext(
                self.llm, use_default_prompt_template, prompt_template_dir
            )
        )
        self.solve_atom_question = (
            solve_atom_question
            if solve_atom_question
            else SolveQuestionWithContext(
                self.llm, use_default_prompt_template, prompt_template_dir
            )
        )
        extra_info_fetch_tools = []
        extra_info_fetch_tools.extend(
            self.solve_parent_question.get_extra_info_fetch_tools()
        )
        extra_info_fetch_tools.extend(
            self.solve_atom_question.get_extra_info_fetch_tools()
        )

        super().__init__(extra_info_fetch_tools, intermediate_process_tools)

    async def rewrite_question_if_need(self, question: Question):
        if len(question.dependencies) > 0:
            await asyncio.create_task(self.is_question_deps_ready(question))
            rewrited_question = self.rewrite_question.forward(question)
            if self.use_en_log:
                info_dict = {
                    "status": f"Rewriting Question",
                    "log_info": f"Original Question: {question.question}. Rewrited Question: {rewrited_question}\n{str(question)}",
                }
            else:
                info_dict = {
                    "status": f"重写问题",
                    "log_info": f"原问题: {question.question}. 重写后的问题: {rewrited_question}\n{str(question)}",
                }
            self.process_intermediate_info(info_dict)
            current_question = Question(
                rewrited_question,
                question.dependencies,
                question.children,
                question.parent,
                question.context,
                question.global_context,
            )
            return current_question
        else:
            return question

    async def solve_problem_impl(self, question: Question, **kwargs):
        if self.use_en_log:
            info_dict = {
                "status": "start solve_problem_impl",
                "log_info": f"current question depth: {question.get_current_depth()}\n{str(question)}",
            }
        else:
            info_dict = {
                "status": "开始处理问题",
                "log_info": f"当前问题深度: {question.get_current_depth()}\n{str(question)}",
            }
        self.process_intermediate_info(info_dict)

        current_question = await self.rewrite_question_if_need(question)

        if (
            current_question.get_current_depth() <= self.max_depth
        ):  # and not self.is_atom_question.forward(current_question):
            info_dict = {
                "status": "Divide Question",
                "log_info": f"Divide Question by Logic: {current_question.question}.",
            }
            self.process_intermediate_info(info_dict)

            children_questions = self.divide_question.forward(current_question)

            children_questions = self.check_divide_question.forward(
                current_question, children_questions
            )

            # display child question
            for child_idx, child_question in enumerate(children_questions):
                info_dict = {
                    "status": f"After Division, sub question{child_idx}",
                    "log_info": str(child_question),
                }
                self.process_intermediate_info(info_dict)

            # iteratively call
            # 递归在这里，从上到下递归
            question_task_dict = {}
            for child_question in children_questions:
                question_task_dict[child_question] = asyncio.create_task(
                    self.solve_problem_impl(child_question)
                )

            # merge children questions
            children_answers_context = ""
            for child_question, c_task in question_task_dict.items():
                child_answer = await c_task
                children_answers_context += f"{child_question}:{child_answer}\n"
                child_question.answer = child_answer

                info_dict = {
                    "status": f"child_question {child_question.question} solved",
                    "log_info": f"child_answer: {child_answer}.",
                }
                self.process_intermediate_info(info_dict)

            parent_question = Question(
                question=question.question, context=children_answers_context
            )

            answer = self.solve_parent_question.forward(parent_question)
            return answer
        else:
            atom_question = Question(
                question=current_question.question,
                context=current_question.global_context,
            )
            atom_answer = self.solve_atom_question.forward(atom_question)
            return atom_answer
