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

from typing import Optional

from nn4k.invoker import NNInvoker, LLMInvoker
from nn4k.executor import NNExecutor
from nn4k.nnhub import SimpleNNHub


class StubInvoker(LLMInvoker):
    @classmethod
    def from_config(cls, nn_config: dict) -> "StubInvoker":
        """
        Create a StubInvoker instance from `nn_config`.
        """
        invoker = cls(nn_config)
        return invoker


class NotInvoker:
    pass


class StubExecutor(NNExecutor):
    def load_model(self, args=None, mode=None, **kwargs):
        self.load_model_called = True

    def warmup_inference(self, args=None, **kwargs):
        self.warmup_inference_called = True

    def inference(self, data, args=None, **kwargs):
        return self.inference_result

    @classmethod
    def from_config(cls, nn_config: dict) -> "StubExecutor":
        """
        Create a StubExecutor instance from `nn_config`.
        """
        executor = cls(nn_config)
        return executor


class StubHub(SimpleNNHub):
    def get_invoker(self, nn_config: dict) -> Optional[NNInvoker]:
        nn_name = nn_config.get("nn_name")
        if nn_name is not None and nn_name == "invoker_test_stub":
            invoker = StubInvoker(nn_config, test_stub_invoker=True)
            return invoker
        return super().get_invoker(nn_config)

    def get_model_executor(
        self, name: str, version: str = None
    ) -> Optional[NNExecutor]:
        if name == "invoker_test_stub":
            if version is None:
                version = "default"
            executor = StubExecutor(
                {"nn_name": name, "nn_version": version}, test_stub_executor=True
            )
            return executor
        return super().get_model_executor(name, version)
