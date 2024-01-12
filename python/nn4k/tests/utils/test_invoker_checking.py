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

import unittest


class TestInvokerChecking(unittest.TestCase):
    """
    module nn4k.utils.invoker_checking unittest
    """

    def testIsOpenAIInvoker(self):
        from nn4k.utils.invoker_checking import is_openai_invoker

        self.assertTrue(is_openai_invoker({"nn_name": "gpt-3.5-turbo"}))
        self.assertTrue(is_openai_invoker({"nn_name": "gpt-4"}))
        self.assertFalse(is_openai_invoker({"nn_name": "dummy"}))

        self.assertTrue(is_openai_invoker({"openai_api_key": "EMPTY"}))
        self.assertTrue(
            is_openai_invoker({"openai_api_base": "http://localhost:38000/v1"})
        )
        self.assertTrue(is_openai_invoker({"openai_max_tokens": 1000}))
        self.assertTrue(is_openai_invoker({"openai_organization": "test_org"}))
        self.assertFalse(is_openai_invoker({"foo": "bar"}))

    def testIsLocalInvoker(self):
        import os
        from nn4k.utils.invoker_checking import is_local_invoker

        dir_path = os.path.dirname(os.path.abspath(__file__))
        self.assertFalse(is_local_invoker({"nn_name": dir_path}))

        model_dir_path = os.path.join(dir_path, "test_model_dir")
        self.assertTrue(is_local_invoker({"nn_name": model_dir_path}))

        self.assertFalse(is_local_invoker({"nn_name": "/not_exists"}))
        self.assertFalse(is_local_invoker({"foo": "bar"}))


if __name__ == "__main__":
    unittest.main()
