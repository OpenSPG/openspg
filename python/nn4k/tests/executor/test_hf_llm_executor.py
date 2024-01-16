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

from nn4k.executor.hugging_face import HfLLMExecutor


class TestHfLLMExecutor(unittest.TestCase):
    """
    HfLLMExecutor unittest
    """

    def setUp(self):
        self._saved_torch = sys.modules.get("torch")
        self._mocked_torch = unittest.mock.MagicMock()
        sys.modules["torch"] = self._mocked_torch

        self._saved_transformers = sys.modules.get("transformers")
        self._mocked_transformers = unittest.mock.MagicMock()
        sys.modules["transformers"] = self._mocked_transformers

    def tearDown(self):
        del sys.modules["torch"]
        if self._saved_torch is not None:
            sys.modules["torch"] = self._saved_torch

        del sys.modules["transformers"]
        if self._saved_transformers is not None:
            sys.modules["transformers"] = self._saved_transformers

    def testHfLLMExecutor(self):
        nn_config = {
            "nn_name": "/opt/test_model_dir",
            "nn_version": "default",
        }

        executor = HfLLMExecutor.from_config(nn_config)
        executor.load_model()
        executor.inference("input")

        self._mocked_transformers.AutoTokenizer.from_pretrained.assert_called()
        self._mocked_transformers.AutoModelForCausalLM.from_pretrained.assert_called()


if __name__ == "__main__":
    unittest.main()
