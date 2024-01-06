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


class TestBaseInvoker(unittest.TestCase):
    """
    NNInvoker and LLMInvoker unittest
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

    def testCustomNNInvoker(self):
        from nn4k.invoker import NNInvoker
        from test_stub import StubInvoker

        nn_config = {"nn_invoker": "test_stub.StubInvoker"}
        invoker = NNInvoker.from_config(nn_config)
        self.assertTrue(isinstance(invoker, StubInvoker))
        self.assertEqual(invoker.init_args, nn_config)
        self.assertEqual(invoker.kwargs, {})

        with self.assertRaises(RuntimeError):
            invoker = NNInvoker.from_config({"nn_invoker": "test_stub.NotInvoker"})

    def testHubInvoker(self):
        from nn4k.invoker import NNInvoker
        from test_stub import StubInvoker

        nn_config = {"nn_name": "test_stub"}
        invoker = NNInvoker.from_config(nn_config)
        self.assertTrue(isinstance(invoker, StubInvoker))
        self.assertEqual(invoker.init_args, nn_config)
        self.assertEqual(invoker.kwargs, {"test_stub_invoker": True})

    def testInvokerNotExists(self):
        from nn4k.invoker import NNInvoker

        with self.assertRaises(RuntimeError):
            invoker = NNInvoker.from_config({"nn_name": "not_exists"})

    def testLocalInvoker(self):
        from nn4k.invoker import NNInvoker
        from test_stub import StubInvoker

        nn_config = {"nn_name": "test_stub"}
        invoker = NNInvoker.from_config(nn_config)
        self.assertTrue(isinstance(invoker, StubInvoker))
        self.assertEqual(invoker.init_args, nn_config)
        self.assertEqual(invoker.kwargs, {"test_stub_invoker": True})

        invoker.warmup_local_model()
        invoker._nn_executor.inference_result = "inference result"
        result = invoker.local_inference("input")
        self.assertEqual(result, invoker._nn_executor.inference_result)


if __name__ == "__main__":
    unittest.main()
