# Copyright 2023 Ant Group CO., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

from typing import Optional

from nn4k.invoker import NNInvoker


class OpenAIInvoker(NNInvoker):
    def __init__(self, nn_config: dict):
        super().__init__(nn_config)

        import openai
        from nn4k.consts import NN_OPENAI_MODEL_NAME_KEY, NN_OPENAI_MODEL_NAME_TEXT
        from nn4k.consts import NN_OPENAI_API_KEY_KEY, NN_OPENAI_API_KEY_TEXT
        from nn4k.consts import NN_OPENAI_API_BASE_KEY, NN_OPENAI_API_BASE_TEXT
        from nn4k.consts import NN_OPENAI_MAX_TOKENS_KEY, NN_OPENAI_MAX_TOKENS_TEXT
        from nn4k.utils.config_parsing import get_string_field
        from nn4k.utils.config_parsing import get_positive_int_field

        self.openai_model_name = get_string_field(
            self.init_args, NN_OPENAI_MODEL_NAME_KEY, NN_OPENAI_MODEL_NAME_TEXT
        )
        self.openai_api_key = get_string_field(
            self.init_args, NN_OPENAI_API_KEY_KEY, NN_OPENAI_API_KEY_TEXT
        )
        self.openai_api_base = get_string_field(
            self.init_args, NN_OPENAI_API_BASE_KEY, NN_OPENAI_API_BASE_TEXT
        )
        self.openai_max_tokens = get_positive_int_field(
            self.init_args, NN_OPENAI_MAX_TOKENS_KEY, NN_OPENAI_MAX_TOKENS_TEXT
        )

        openai.api_key = self.openai_api_key
        openai.api_base = self.openai_api_base

    @classmethod
    def from_config(cls, nn_config: dict) -> "OpenAIInvoker":
        invoker = cls(nn_config)
        return invoker

    def _create_prompt(self, input, **kwargs):
        if isinstance(input, list):
            prompt = input
        else:
            prompt = [input]
        return prompt

    def _create_output(self, input, prompt, completion, **kwargs):
        output = [choice.text for choice in completion.choices]
        return output

    def remote_inference(
        self, input, max_output_length: Optional[int] = None, **kwargs
    ):
        import openai

        if max_output_length is None:
            max_output_length = self.openai_max_tokens
        prompt = self._create_prompt(input, **kwargs)
        completion = openai.Completion.create(
            model=self.openai_model_name,
            prompt=prompt,
            max_tokens=max_output_length,
        )
        output = self._create_output(input, prompt, completion, **kwargs)
        return output
