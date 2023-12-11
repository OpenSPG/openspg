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

from typing import Any
from typing import Union

from nn4k.invoker import NNInvoker


class OpenAIInvoker(NNInvoker):
    @classmethod
    def _preprocess_config(cls, nn_config: Union[str, dict]) -> dict:
        try:
            if isinstance(nn_config, str):
                with open(nn_config, "r") as f:
                    nn_config = json.load(f)
        except:
            raise ValueError("cannot decode config file")
        return nn_config

    @classmethod
    def _get_field(cls, nn_config: dict, name: str, text: str) -> Any:
        value = nn_config.get(name)
        if value is None:
            message = "%s %r not found" % (text, name)
            raise ValueError(message)
        return value

    @classmethod
    def _get_string_field(cls, nn_config: dict, name: str, text: str) -> str:
        value = cls._get_field(nn_config, name, text)
        if not isinstance(value, str):
            message = "%s %r must be string; " % (text, name)
            message += "%r is invalid" % (value,)
            raise TypeError(message)
        return value

    @classmethod
    def _get_int_field(cls, nn_config: dict, name: str, text: str) -> int:
        value = cls._get_field(nn_config, name, text)
        if not isinstance(value, int):
            message = "%s %r must be integer; " % (text, name)
            message += "%r is invalid" % (value,)
            raise TypeError(message)
        return value

    @classmethod
    def _get_positive_int_field(cls, nn_config: dict, name: str, text: str) -> int:
        value = cls._get_int_field(nn_config, name, text)
        if value <= 0:
            message = "%s %r must be positive integer; " % (text, name)
            message += "%r is invalid" % (value,)
            raise ValueError(message)
        return value

    @classmethod
    def _parse_config(cls, nn_config: dict) -> dict:
        openai_api_key = cls._get_string_field(
            nn_config, "openai_api_key", "openai api key"
        )
        openai_api_base = cls._get_string_field(
            nn_config, "openai_api_base", "openai api base"
        )
        openai_model_name = cls._get_string_field(
            nn_config, "openai_model_name", "openai model name"
        )
        openai_max_tokens = cls._get_positive_int_field(
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

        nn_config = cls._preprocess_config(nn_config)
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
        prompt = input
        return prompt

    def _create_output(self, input, prompt, completion, **kwargs):
        output = prompt + completion.choices[0].text
        return output

    def remote_inference(self, input, **kwargs):
        import openai

        prompt = self._create_prompt(input, **kwargs)
        completion = openai.Completion.create(
            model=self._openai_model_name,
            prompt=prompt,
            max_tokens=self._openai_max_tokens,
        )
        output = self._create_output(input, prompt, completion, **kwargs)
        return output
