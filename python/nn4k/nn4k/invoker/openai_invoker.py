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

from typing import Union, Optional
from dataclasses import dataclass, field

from nn4k.invoker import NNInvokerConfig, NNInvoker


@dataclass
class OpenAIInvokerConfig(NNInvokerConfig):
    openai_api_key: str = field(default=None, metadata={"help": "OpenAI API key"})

    openai_api_base: str = field(default=None, metadata={"help": "OpenAI API base URL"})

    openai_model_name: str = field(
        default=None, metadata={"help": "name of ChatGPT model"}
    )

    openai_max_tokens: int = field(
        default=None, metadata={"help": "maximum number of tokens to generate"}
    )


class OpenAIInvoker(NNInvoker):
    @classmethod
    def try_parse_config(
        cls, nn_config: Union[str, dict]
    ) -> Optional[OpenAIInvokerConfig]:
        from nn4k.utils.config_parsing import preprocess_config
        from nn4k.utils.config_parsing import get_string_field
        from nn4k.utils.config_parsing import get_positive_int_field

        nn_config = preprocess_config(nn_config)
        invoker_type = nn_config.get("invoker_type")
        if invoker_type != "OpenAI":
            return None

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

        config = OpenAIInvokerConfig(
            openai_api_key=openai_api_key,
            openai_api_base=openai_api_base,
            openai_model_name=openai_model_name,
            openai_max_tokens=openai_max_tokens,
        )
        return config

    @classmethod
    def _from_config(cls, nn_config: OpenAIInvokerConfig) -> "OpenAIInvoker":
        import openai

        o = super()._from_config(nn_config)
        openai.api_key = o._nn_config.openai_api_key
        openai.api_base = o._nn_config.openai_api_base
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
            max_output_length = self._nn_config.openai_max_tokens
        prompt = self._create_prompt(input, **kwargs)
        completion = openai.Completion.create(
            model=self._nn_config.openai_model_name,
            prompt=prompt,
            max_tokens=max_output_length,
        )
        output = self._create_output(input, prompt, completion, **kwargs)
        return output
