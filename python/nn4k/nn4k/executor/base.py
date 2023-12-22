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

from abc import ABC, abstractmethod


class NNExecutor(ABC):
    """
    Entry point of model execution in a certain pod.
    """

    @classmethod
    def from_config(cls, nn_config, **kwargs):
        pass

    def __init__(self, backend_model, backend_tokenizer, init_args, **kwargs):
        self.backend_model = backend_model
        self.backend_tokenizer = backend_tokenizer
        self.init_args = init_args
        self.kwargs = kwargs


class LLMExecutor(NNExecutor):
    @classmethod
    def from_config(cls, nn_config: dict, **kwargs):
        """
        Args:
            nn_config
        """

        if "nn_name" in nn_config:
            from nn4k.executor.hugging_face import HfLLMExecutor

            return HfLLMExecutor.from_config(nn_config)
        else:
            o = cls.__new__(cls)
            o._nn_config = nn_config
            return o

    @abstractmethod
    def execute_sft(self, args=None, callbacks=None, **kwargs):
        """
        The entry point of SFT execution in a certain pod.
        """
        raise NotImplementedError(f"{self.__class__.__name__} does not support SFT.")

    @abstractmethod
    def execute_rl_tuning(self, args=None, callbacks=None, **kwargs):
        """
        The entry point of SFT execution in a certain pod.
        """
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support RL-Tuning."
        )

    def execute_inference(self, args, **kwargs):
        dataset = args.parse_dataset()
        ret = []
        for d in dataset:
            ret.append(self.inference(d))
        return ret

    @abstractmethod
    def inference(self, data, **kwargs):
        """
        The entry point of inference. Usually for local invokers or model services.
        """
        raise NotImplementedError()
