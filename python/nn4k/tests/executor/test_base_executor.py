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

import os
import sys
import unittest


class TestBaseExecutor(unittest.TestCase):
    """
    NNExecutor and LLMExecutor unittest
    """

    def setUp(self):
        # for importing test_stub.py
        dir_path = os.path.dirname(os.path.abspath(__file__))
        sys.path.insert(0, dir_path)

        from nn4k.nnhub import NNHub
        from test_stub import StubHub

        NNHub._hub_instance = StubHub()

    def tearDown(self):
        from nn4k.nnhub import NNHub

        sys.path.pop(0)
        NNHub._hub_instance = None

    def testCustomNNExecutor(self):
        from nn4k.executor import NNExecutor
        from test_stub import StubExecutor

        nn_config = {"nn_executor": "test_stub.StubExecutor"}
        executor = NNExecutor.from_config(nn_config)
        self.assertTrue(isinstance(executor, StubExecutor))
        self.assertEqual(executor.init_args, nn_config)
        self.assertEqual(executor.kwargs, {})

        with self.assertRaises(RuntimeError):
            executor = NNExecutor.from_config({"nn_executor": "test_stub.NotExecutor"})

    def testHubExecutor(self):
        from nn4k.executor import NNExecutor
        from test_stub import StubExecutor

        nn_config = {"nn_name": "test_stub", "nn_version": "default"}
        executor = NNExecutor.from_config(nn_config)
        self.assertTrue(isinstance(executor, StubExecutor))
        self.assertEqual(executor.init_args, nn_config)
        self.assertEqual(executor.kwargs, {"test_stub_executor": True})

    def testExecutorNotExists(self):
        from nn4k.executor import NNExecutor

        with self.assertRaises(RuntimeError):
            executor = NNExecutor.from_config({"nn_name": "not_exists"})


if __name__ == "__main__":
    unittest.main()
