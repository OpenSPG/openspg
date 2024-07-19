from knext.ca.common.base import Question, KagBaseModule


class Solver(KagBaseModule):
    def __init__(self, llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn, is_computational=True):
        super().__init__(
            llm_module=llm_module,
            use_default_prompt_template=use_default_prompt_template,
            is_prompt_template_cn=is_prompt_template_cn,
            prompt_template_dir=prompt_template_dir,
            is_computational=is_computational
        )


class SolveQuestionWithContext(Solver):
    """
    Module for answering questions based on context using a language model.

    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None, is_prompt_template_cn=True):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir, is_prompt_template_cn)

    def get_module_name(self):
        return "SolveQuestionWithContext"

    def get_template_var_names(self):
        return ['question', 'context']

    def get_extra_info_fetch_tools(self):
        return []

    def preprocess(self, question: Question):
        prompt = self.state_dict['prompt_template'].substitute(
            question=question.question,
            context=question.context,
        )
        return prompt

    def postprocess(self, question: Question, llm_output):
        return llm_output.strip()

