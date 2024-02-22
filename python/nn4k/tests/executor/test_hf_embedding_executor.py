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

from nn4k.executor.huggingface.hf_embedding_executor import HFEmbeddingExecutor


class TestHFEmbeddingExecutor(unittest.TestCase):
    """
    HFEmbeddingExecutor unittest
    """

    def setUp(self):
        self._saved_torch = sys.modules.get("torch")
        self._mocked_torch = unittest.mock.MagicMock()
        sys.modules["torch"] = self._mocked_torch

        self._saved_sentence_transformers = sys.modules.get("sentence_transformers")
        self._mocked_sentence_transformers = unittest.mock.MagicMock()
        sys.modules["sentence_transformers"] = self._mocked_sentence_transformers

    def tearDown(self):
        del sys.modules["torch"]
        if self._saved_torch is not None:
            sys.modules["torch"] = self._saved_torch

        del sys.modules["sentence_transformers"]
        if self._saved_sentence_transformers is not None:
            sys.modules["sentence_transformers"] = self._saved_sentence_transformers

    def testHFEmbeddingExecutor(self):
        nn_config = {
            "nn_name": "/opt/test_model_dir",
            "nn_version": "default",
        }

        executor = HFEmbeddingExecutor.from_config(nn_config)
        executor.load_model()
        executor.inference("input")

        self._mocked_sentence_transformers.SentenceTransformer.assert_called()
        executor.model.encode.assert_called()


if __name__ == "__main__":
    unittest.main()
