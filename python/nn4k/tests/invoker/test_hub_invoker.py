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

import json
import requests
import unittest
import unittest.mock

from nn4k.invoker import NNInvoker
from nn4k.invoker.hub_invoker import NN4KException


class TestHubInvoker(unittest.TestCase):
    """
    HubInvoker unittest.
    """

    def setUp(self):
        self._save_response = requests.Response
        self._mock_response = unittest.mock.MagicMock()
        self._mock_response.status_code = 200
        self._mock_response.json.return_value = {
            "model_name": "vllm_model",
            "model_version": "1",
            "text_output": "Long long ago, 100 years ago, ",
        }

        self._nn_config = {
            "hub_infer_url": "hub_infer_url",
            "generate_config": {
                "ignore_eos": True,
                "skip_special_tokens": True,
                "use_beam_search": True,
                "best_of": 3,
                "max_tokens": 127,
                "n": 1,
                "top_k": -1,
                "frequency_penalty": 0.0,
                "length_penalty": 1.0,
                "presence_penalty": 0.0,
                "temperature": 0.0,
                "top_p": 1.0,
            },
        }
        self._invoker = NNInvoker.from_config(self._nn_config)

    def tearDown(self):
        requests.Response = self._save_response

    def testHubInvokerCallService(self):

        self.assertEqual(self._invoker.init_args, self._nn_config)
        self.assertEqual(
            self._invoker.invoker_args.generate_config,
            self._nn_config["generate_config"],
        )

        with unittest.mock.patch(
            "requests.post", return_value=self._mock_response
        ) as mock_post:
            result = self._invoker.remote_inference("Long long ago, ")

            data = {
                "text_input": "Long long ago, ",
                "parameters": self._invoker.invoker_args.generate_config,
            }

            mock_post.assert_called_with(
                url=self._invoker.invoker_args.hub_infer_url,
                headers={"Content-Type": "application/json"},
                data=json.dumps(data),
            )
            index = self._mock_response.json()["text_output"].find("\n")
            self.assertEqual(
                result, [self._mock_response.json()["text_output"][index + 1 :]]
            )

    def testHubInvokerWithNN4KException(self):
        with self.assertRaises(NN4KException):
            with unittest.mock.patch(
                "requests.post", return_value=self._mock_response
            ) as mock_post:
                self._mock_response.status_code = 500
                result = self._invoker.remote_inference("Long long ago, ")

    def testHubInvokerWithNotImplementException(self):
        with self.assertRaises(NotImplementedError):
            with unittest.mock.patch(
                "requests.post", return_value=self._mock_response
            ) as mock_post:
                self._mock_response.status_code = 200
                result = self._invoker.remote_inference(
                    [
                        "Long long ago, ",
                    ]
                )


if __name__ == "__main__":
    unittest.main()
