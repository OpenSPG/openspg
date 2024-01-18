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

import sys
import unittest
import unittest.mock
from dataclasses import dataclass

from nn4k.invoker import NNInvoker


@dataclass
class MockCompletion:
    choices: list


@dataclass
class MockMessage:
    content: str


@dataclass
class MockChoice:
    message: MockMessage


class TestOpenAIInvokerLegacyAPI(unittest.TestCase):
    """
    OpenAIInvoker unittest for legacy OpenAI api
    """

    def setUp(self):
        self._saved_openai = sys.modules.get("openai")
        self._mocked_openai = unittest.mock.MagicMock()
        sys.modules["openai"] = self._mocked_openai

    def tearDown(self):
        del sys.modules["openai"]
        if self._saved_openai is not None:
            sys.modules["openai"] = self._saved_openai

    def testOpenAICompletion(self):
        self._mocked_openai.__version__ = "0.28.1"

        nn_config = {
            "nn_name": "gpt-3.5-turbo",
            "openai_api_key": "EMPTY",
            "openai_api_base": "http://localhost:38080/v1",
            "openai_max_tokens": 2000,
        }
        invoker = NNInvoker.from_config(nn_config)
        self.assertEqual(invoker.init_args, nn_config)
        self.assertEqual(self._mocked_openai.api_key, nn_config["openai_api_key"])
        self.assertEqual(self._mocked_openai.api_base, nn_config["openai_api_base"])

        mock_completion = MockCompletion(
            choices=[MockChoice(message=MockMessage(content="a dog named Bolt ..."))]
        )
        self._mocked_openai.ChatCompletion.create.return_value = mock_completion

        result = invoker.remote_inference("Long long ago, ")
        self._mocked_openai.ChatCompletion.create.assert_called_with(
            model=nn_config["nn_name"],
            messages=[{"role": "user", "content": "Long long ago, "}],
            max_tokens=nn_config["openai_max_tokens"],
        )
        self.assertEqual(result, [mock_completion.choices[0].message.content])


if __name__ == "__main__":
    unittest.main()
