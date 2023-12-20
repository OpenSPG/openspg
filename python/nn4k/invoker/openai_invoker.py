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

from typing import Union

from nn4k.invoker import NNInvoker


class OpenAIInvoker(NNInvoker):
    @classmethod
    def _parse_config(cls, nn_config: dict) -> dict:
        from nn4k.utils.config_parsing import get_string_field
        from nn4k.utils.config_parsing import get_positive_int_field

        openai_api_key = get_string_field(nn_config, "openai_api_key", "openai api key")
        openai_api_base = get_string_field(
            nn_config, "openai_api_base", "openai api base"
        )
        openai_model_name = get_string_field(
            nn_config, "openai_model_name", "openai model name"
        )
        openai_max_tokens = get_positive_int_field(
            nn_config, "openai_max_tokens", "openai max tokens"
        )
        config = dict(
            openai_api_key=openai_api_key,
            openai_api_base=openai_api_base,
            openai_model_name=openai_model_name,
            openai_max_tokens=openai_max_tokens,
        )
        return config

    @classmethod
    def from_config(cls, nn_config: Union[str, dict]):
        import openai
        from nn4k.utils.config_parsing import preprocess_config

        nn_config = preprocess_config(nn_config)
        config = cls._parse_config(nn_config)

        o = cls.__new__(cls)
        o._openai_api_key = config["openai_api_key"]
        o._openai_api_base = config["openai_api_base"]
        o._openai_model_name = config["openai_model_name"]
        o._openai_max_tokens = config["openai_max_tokens"]

        openai.api_key = o._openai_api_key
        openai.api_base = o._openai_api_base
        return o

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
            max_output_length = self._openai_max_tokens
        prompt = self._create_prompt(input, **kwargs)
        completion = openai.Completion.create(
            model=self._openai_model_name,
            prompt=prompt,
            max_tokens=max_output_length,
        )
        output = self._create_output(input, prompt, completion, **kwargs)
        return output
