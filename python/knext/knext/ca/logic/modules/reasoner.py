from knext.ca.common.base import Question, KagBaseModule


class AnswerQuestionWithContext(KagBaseModule):
    """
    Module for answering questions based on context using a language model.

    """
    def __init__(self, llm_module, use_default_prompt_template=True, prompt_template_dir=None):
        super().__init__(llm_module, use_default_prompt_template, prompt_template_dir)

    def get_module_name(self):
        return "AnswerQuestionWithContext" 

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

