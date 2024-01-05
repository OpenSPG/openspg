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
from typing import Union


class NNExecutor(ABC):
    """
    Entry point of model execution in a certain pod.
    """

    def __init__(self, init_args: dict, **kwargs):
        self._init_args = init_args
        self._kwargs = kwargs
        self._model = None
        self._tokenizer = None

    @property
    def init_args(self):
        """
        Return the `init_args` passed to the executor constructor.
        """
        return self._init_args

    @property
    def kwargs(self):
        """
        Return the `kwargs` passed to the executor constructor.
        """
        return self._kwargs

    @property
    def model(self):
        """
        Return the model object managed by this executor.

        :raises RuntimeError: if the model is not initialized yet
        """
        if self._model is None:
            message = "model is not initialized yet"
            raise RuntimeError(message)
        return self._model

    @property
    def tokenizer(self):
        """
        Return the tokenizer object managed by this executor.

        :raises RuntimeError: if the tokenizer is not initialized yet
        """
        if self._tokenizer is None:
            message = "tokenizer is not initialized yet"
            raise RuntimeError(message)
        return self._tokenizer

    def execute_inference(self, args=None, **kwargs):
        """
        The entry point of batch inference in a certain pod.
        """
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support batch inference."
        )

    def inference(self, data, args=None, **kwargs):
        """
        The entry point of inference. Usually for local invokers or model services.
        """
        raise NotImplementedError()

    @abstractmethod
    def load_model(self, args=None, mode=None, **kwargs):
        """
        Implement model loading logic in derived executor classes.

        Implementer should initialize `self._model` and `self._tokenizer`.

        This method will be called by several entry methods in executors and invokers.
        """
        raise NotImplementedError()

    def warmup_inference(self, args=None, **kwargs):
        """
        Implement model warming up logic for inference in derived executor classes.
        """
        pass

    @classmethod
    @abstractmethod
    def from_config(cls, nn_config: Union[str, dict]) -> "NNExecutor":
        """
        Create an NN executor instance from `nn_config`.

        This method is abstract, derived class must override it by either
        creating executor instances or implementating dispatch logic.

        :param nn_config: config to use, can be dictionary or path to a JSON file
        :type nn_config: str or dict
        :rtype: NNExecutor
        """
        from nn4k.nnhub import NNHub
        from nn4k.consts import NN_NAME_KEY, NN_NAME_TEXT
        from nn4k.consts import NN_VERSION_KEY, NN_VERSION_TEXT
        from nn4k.consts import NN_EXECUTOR_KEY, NN_EXECUTOR_TEXT
        from nn4k.utils.config_parsing import preprocess_config
        from nn4k.utils.config_parsing import get_string_field
        from nn4k.utils.class_importing import dynamic_import_class

        nn_config = preprocess_config(nn_config)
        nn_executor = nn_config.get(NN_EXECUTOR_KEY)
        if nn_executor is not None:
            nn_executor = get_string_field(nn_config, NN_EXECUTOR_KEY, NN_EXECUTOR_TEXT)
            executor_class = dynamic_import_class(nn_executor, NN_EXECUTOR_TEXT)
            if not issubclass(executor_class, NNExecutor):
                message = "%r is not an %s class" % (nn_executor, NN_EXECUTOR_TEXT)
                raise RuntimeError(message)
            executor = executor_class.from_config(nn_config)
            return executor

        nn_name = nn_config.get(NN_NAME_KEY)
        if nn_name is not None:
            nn_name = get_string_field(nn_config, NN_NAME_KEY, NN_NAME_TEXT)
        nn_version = nn_config.get(NN_VERSION_KEY)
        if nn_version is not None:
            nn_version = get_string_field(nn_config, NN_VERSION_KEY, NN_VERSION_TEXT)
        if nn_name is not None:
            hub = NNHub.get_instance()
            executor = hub.get_model_executor(nn_name, nn_version)
            if executor is not None:
                return executor

        message = "can not create executor for NN config"
        if nn_name is not None:
            message += "; model: %r" % nn_name
            if nn_version is not None:
                message += ", version: %r" % nn_version
        raise RuntimeError(message)


class LLMExecutor(NNExecutor):
    def execute_sft(self, args=None, callbacks=None, **kwargs):
        """
        The entry point of SFT execution in a certain pod.
        """
        raise NotImplementedError(f"{self.__class__.__name__} does not support SFT.")

    def execute_rl_tuning(self, args=None, callbacks=None, **kwargs):
        """
        The entry point of SFT execution in a certain pod.
        """
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support RL-Tuning."
        )
