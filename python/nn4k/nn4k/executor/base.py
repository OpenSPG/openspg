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
    def from_config(cls, nn_config: dict, **kwargs):
        """
        Create NNExecutor instances from `nn_config`.
        """

        if "nn_name" in nn_config:
            from nn4k.executor.hugging_face import HfLLMExecutor

            return HfLLMExecutor.from_config(nn_config)
        else:
            o = cls.__new__(cls)
            o._nn_config = nn_config
            return o

    def __init__(
        self, model=None, tokenizer=None, init_args=None, inference_args=None, **kwargs
    ):
        self._model = model
        self._tokenizer = tokenizer
        self._init_args = init_args
        self._inference_args = inference_args
        self._kwargs = kwargs

    @property
    def model(self):
        """
        Return the model object managed by this executor.

        :raises RuntimeError: if the model is not loaded yet
        """
        if self._model is None:
            message = "model is not loaded yet"
            raise RuntimeError(message)
        return self._model

    @property
    def tokenizer(self):
        """
        Return the tokenizer object managed by this executor.

        :raises RuntimeError: if the tokenizer is not loaded yet
        """
        if self._tokenizer is None:
            message = "tokenizer is not loaded yet"
            raise RuntimeError(message)
        return self._tokenizer

    @property
    def init_args(self):
        """
        Return the `init_args` passed to the executor constructor.
        """
        return self._init_args

    @property
    def inference_args(self):
        """
        Return the `inference_args` passed to the executor constructor.
        """
        return self._inference_args

    @property
    def kwargs(self):
        """
        Return the `kwargs` passed to the executor constructor.
        """
        return self._kwargs

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

    @abstractmethod
    def load_model(self, **kwargs):
        """
        Implement model loading logic in derived executor classes.

        Implementer should initialize `self._model` and `self._tokenizer` to non-None
        values according to `self._init_args`, `self._inference_args`, `self._kwargs`
        and `kwargs`.

        This method will be called by several entry methods in executors and invokers.
        """
        raise NotImplementedError()

    def warmup_inference(self, **kwargs):
        """
        Implement model warming up logic for inference in derived executor classes.
        """
        pass


class LLMExecutor(NNExecutor):
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
