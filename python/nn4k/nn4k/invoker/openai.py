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

from nn4k.invoker import NNInvoker


class OpenAIInvoker(NNInvoker):
    def __init__(self, nn_config: dict):
        super().__init__(nn_config)

        import openai
        from nn4k.utils.config_parsing import get_string_field
        from nn4k.utils.config_parsing import get_positive_int_field

        self.openai_model_name = get_string_field(
            self.init_args, "nn_name", "openai model name"
        )
        self.openai_api_key = get_string_field(
            self.init_args, "openai_api_key", "openai api key"
        )
        self.openai_api_base = get_string_field(
            self.init_args, "openai_api_base", "openai api base"
        )
        self.openai_max_tokens = get_positive_int_field(
            self.init_args, "openai_max_tokens", "openai max tokens"
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

    def remote_inference(self, input, **kwargs):
        import openai

        if "max_output_length" in kwargs:
            max_output_length = kwargs.pop("max_output_length")
        else:
            max_output_length = self.openai_max_tokens
        prompt = self._create_prompt(input, **kwargs)
        completion = openai.Completion.create(
            model=self.openai_model_name,
            prompt=prompt,
            max_tokens=max_output_length,
        )
        output = self._create_output(input, prompt, completion, **kwargs)
        return output
