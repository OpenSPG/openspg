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
from abc import ABC
from typing import Union

from nn4k.executor import LLMExecutor
from nn4k.executor import NNExecutor
from nn4k.nnhub import SimpleNNHub


class NNInvoker(ABC):
    """
    Invoking Entry Interfaces for NN Models.
    One NNInvoker object is for one NN Model.
    - Interfaces starting with "submit_" means submitting a batch task to a remote execution engine.
    - Interfaces starting with "remote_" means querying a remote service for some results.
    - Interfaces starting with "local_"  means running something locally.
            Must call `warmup_local_model` before calling any local_xxx interface.
    """

    hub = SimpleNNHub()

    def __init__(self, nn_executor: NNExecutor) -> None:
        if os.getenv("NN4K_DEBUG") is None:
            raise EnvironmentError(
                "In prod env, only NNInvoker.from_config is allowed for creating an nn_invoker."
            )
        super().__init__()
        self._nn_executor: NNExecutor = nn_executor


class LLMInvoker(NNInvoker):
    def __init__(self, nn_executor: LLMExecutor) -> None:
        super().__init__(nn_executor)

    def submit_inference(self, submit_mode="k8s"):
        # TODO. maybe like:
        # engine.submit(self._nn_config, "xx_executor.execute_inference()")
        pass

    def submit_sft(self, submit_mode="k8s"):
        pass

    def submit_rl_tuning(self, submit_mode="k8s"):
        pass

    # def deploy(cls, args, deploy_mode='k8s'):
    #     pass

    def remote_inference(self, input, **kwargs):
        """
        这个是从已有的服务中获取inference
        Args:
            args:
            **kwargs:

        Returns:

        """
        # TODO . maybe like:
        # service = self.hub.get_service()
        # return service.query_xx(input)
        pass

    def local_inference(self, data, **kwargs):
        return self._nn_executor.inference(data, **kwargs)

    def warmup_local_model(self):
        name = self._nn_config.get("nn_name")
        version = self._nn_config.get("nn_version")
        executor = self.hub.get_model_executor(name, version)
        if executor is None:
            message = "model %r version %r " % (name, version)
            message += "is not found in the model hub"
            raise RuntimeError(message)
        self._nn_executor: LLMExecutor = executor
        self._nn_executor.load_model()
        self._nn_executor.warmup_inference()

    @classmethod
    def from_config(cls, nn_config: Union[str, dict]):
        from nn4k.utils.config_parsing import preprocess_config

        nn_config = preprocess_config(nn_config)
        if nn_config.get("invoker_type", "LLM") == "LLM":

            o = cls.__new__(cls)
            o._nn_config = nn_config
            return o
        elif nn_config.get("invoker_type", "LLM") == "OpenAI":
            from nn4k.invoker.openai_invoker import OpenAIInvoker

            return OpenAIInvoker.from_config(nn_config)
