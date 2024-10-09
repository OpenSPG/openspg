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

from knext.ca.common.client import register_llm_client
from openai import OpenAI


@register_llm_client("openai")
class OpenaiClient(object):
    def __init__(
        self,
        model_name=None,
        base_url=None,
        api_key=None,
        **kwargs,
    ):
        self.model_name = model_name
        self.client = OpenAI(api_key=api_key, base_url=base_url, **kwargs)

    def generate(self, prompt, temperature=0.7):
        response = self.client.chat.completions.create(
            model=self.model_name,
            messages=[
                {"role": "system", "content": "You are a helpful assistant"},
                {"role": "user", "content": prompt},
            ],
            stream=False,
            temperature=temperature,
        )
        result = response.choices[0].message.content
        return result
