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

from nn4k.executor import NNExecutor, LLMExecutor
from nn4k.nnhub import SimpleNNHub


class StubExecutor(LLMExecutor):
    def load_model(self, args=None, mode=None, model_to_cuda=True, **kwargs):
        pass

    def warmup_inference(self, args=None, **kwargs):
        pass

    def inference(self, inputs, args=None, **kwargs):
        pass

    @classmethod
    def from_config(cls, nn_config: dict) -> "StubExecutor":
        """
        Create a StubExecutor instance from `nn_config`.
        """
        executor = cls(nn_config)
        return executor


class NotExecutor:
    pass


class StubHub(SimpleNNHub):
    def get_model_executor(
        self, name: str, version: str = None
    ) -> Optional[NNExecutor]:
        if name == "executor_test_stub":
            if version is None:
                version = "default"
            executor = StubExecutor(
                {"nn_name": name, "nn_version": version}, test_stub_executor=True
            )
            return executor
        return super().get_model_executor(name, version)
