import asyncio

from knext.ca.common.base import Question, Agent
from knext.ca.logic.modules.reasoner import AnswerQuestionWithContext
from knext.ca.logic.modules.planner import DivideQuestion, IsAtomQuestion, RewriteQuestionBasedOnDeps


class DivideAndConquerAgent(Agent):
    def __init__(
            self,
            llm,
            intermediate_process_tools,
            max_depth=1,
            divide_question=None,
            rewrite_question=None,
            is_atom_question=None,
            answer_parent_question=None,
            answer_atom_question=None,
            use_default_prompt_template=False,
            prompt_template_dir=None,
            **kwargs
    ):
        self.llm = llm
        self.max_depth = max_depth

        self.divide_question = divide_question if divide_question else DivideQuestion(
            self.llm, use_default_prompt_template, prompt_template_dir)

        self.rewrite_question = rewrite_question if rewrite_question else RewriteQuestionBasedOnDeps(
            self.llm, use_default_prompt_template, prompt_template_dir)

        self.is_atom_question = is_atom_question if is_atom_question else IsAtomQuestion(
            self.llm, use_default_prompt_template, prompt_template_dir)

        self.answer_parent_question = answer_parent_question if answer_parent_question else AnswerQuestionWithContext(
            self.llm, use_default_prompt_template, prompt_template_dir)
        self.answer_atom_question = answer_atom_question if answer_atom_question else AnswerQuestionWithContext(
            self.llm, use_default_prompt_template, prompt_template_dir)
        extra_info_fetch_tools = []
        extra_info_fetch_tools.extend(self.answer_parent_question.get_extra_info_fetch_tools())
        extra_info_fetch_tools.extend(self.answer_atom_question.get_extra_info_fetch_tools())

        super().__init__(extra_info_fetch_tools, intermediate_process_tools)

    async def rewrite_question_if_need(self, question: Question):
        if len(question.dependencies) > 0:
            await asyncio.create_task(self.is_question_deps_ready(question))
            rewrited_question = self.rewrite_question.forward(question)
            info_dict = {
                'status': f'重写问题',
                'log_info': f'原问题: {question.question}. 重写后的问题: {rewrited_question}\n{str(question)}',
            }
            self.process_intermediate_info(info_dict)
            current_question = Question(
                rewrited_question,
                question.dependencies,
                question.children,
                question.parent,
                question.context)
            return current_question
        else:
            return question

    async def solve_problem_impl(self, question: Question, **kwargs):
        info_dict = {
            'status': 'start solve_problem_impl',
            'log_info': f'current question depth: {question.get_current_depth()}\n{str(question)}'
        }
        self.process_intermediate_info(info_dict)

        current_question = await self.rewrite_question_if_need(question)

        if current_question.get_current_depth() <= self.max_depth:  # and not self.is_atom_question.forward(current_question):
            info_dict = {
                'status': 'Divide Question',
                'log_info': f'Divide Question by Logic: {current_question.question}.',
            }
            self.process_intermediate_info(info_dict)

            children_questions = self.divide_question.forward(current_question)

            # display child question
            for child_idx, child_question in enumerate(children_questions):
                info_dict = {
                    'status': f'After Division, sub question{child_idx}',
                    'log_info': str(child_question),
                }
                self.process_intermediate_info(info_dict)

            # iteratively call
            question_task_dict = {}
            for child_question in children_questions:
                question_task_dict[child_question] = asyncio.create_task(self.solve_problem_impl(child_question))

            # merge children questions
            children_answers_context = ""
            for child_question, c_task in question_task_dict.items():
                child_answer = await c_task
                children_answers_context += f'{child_question}:{child_answer}\n'
                child_question.answer = child_answer

                info_dict = {
                    'status': f'child_question {child_question.question} solved',
                    'log_info': f'child_answer: {child_answer}.',
                }
                self.process_intermediate_info(info_dict)

            parent_question = Question(
                question=question.question,
                context=children_answers_context
            )

            answer = self.answer_parent_question.forward(parent_question)
            return answer
        else:
            atom_question = Question(
                question=current_question.question,
            )
            atom_answer = self.answer_atom_question.forward(atom_question)
            return atom_answer
