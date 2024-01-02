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
from typing import Union, Optional
from dataclasses import dataclass, field


@dataclass
class NNExecutorConfig:
    pass


@dataclass
class LLMExecutorConfig(NNExecutorConfig):
    nn_name: str = field(
        default=None, metadata={"help": "name of the NN model to load"}
    )

    nn_version: str = field(
        default="default",
        metadata={"help": "version of the NN model to load; default to 'default'"},
    )

    nn_device: str = field(
        default=None,
        metadata={
            "help": "device of the NN model to place to; default to None for auto selection"
        },
    )

    nn_trust_remote_code: bool = field(
        default=False,
        metadata={
            "help": "whether to trust remote code when loading pretrained model; default to False"
        },
    )


class NNExecutor(ABC):
    """
    Entry point of model execution in a certain pod.
    """

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

    @classmethod
    def try_parse_config(
        cls, nn_config: Union[str, dict]
    ) -> Optional[NNExecutorConfig]:
        """
        Try parse executor config from `nn_config`.

        If the derived executor class accepts config in `nn_config`, it should parse and
        return an instance of `NNExecutorConfig` or its derived class which will be passed
        to `from_config` later, otherwise it should return `None`.

        :param nn_config: config attempting to parse, can be dictionary or path to a JSON file
        :type nn_config: str or dict
        :rtype: NNExecutorConfig or None
        """
        return None

    @classmethod
    def _from_config(cls, nn_config: NNExecutorConfig) -> "NNExecutor":
        """
        Create executor instance from `nn_config`.

        :param nn_config: config instance returned from `try_parse_config`
        :type nn_config: NNExecutorConfig
        :rtype: NNExecutor
        """
        o = cls.__new__(cls)
        o._nn_config = nn_config
        return o

    _registered_executor_classes = []

    @classmethod
    def register_executor_class(cls, executor_class):
        """
        Register an executor class for later use in `from_config`.

        :param executor_class: a derived class of `NNExecutor`
        """
        if not issubclass(executor_class, cls):
            message = "invalid executor class: %r" % (executor_class,)
            raise TypeError(message)
        if executor_class in cls._registered_executor_classes:
            message = "executor class %r has been registered" % (executor_class,)
            raise RuntimeError(message)
        cls._registered_executor_classes.append(executor_class)

    @classmethod
    def from_config(cls, nn_config: Union[str, dict]):
        """
        Try to create an executor instance from `nn_config`.

        The last registered executor class whose `try_parse_config` method
        returns a non-None `NNInvokerConfig` will be used.

        :param nn_config: config to use, can be dictionary or path to a JSON file
        :type nn_config: str or dict
        :rtype: NNInvoker
        :raises RuntimeError: if `nn_config` is not recognized by all the
                              registered executor classes
        """
        from nn4k.utils.config_parsing import preprocess_config

        nn_config = preprocess_config(nn_config)
        for executor_class in reversed(cls._registered_executor_classes):
            config = executor_class.try_parse_config(nn_config)
            if config is not None:
                executor = executor_class._from_config(config)
                return executor
        message = "nn_config is not recognized by all the registered executor classes"
        raise RuntimeError(message)


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

    @classmethod
    def try_parse_config(
        cls, nn_config: Union[str, dict]
    ) -> Optional[LLMExecutorConfig]:
        from nn4k.utils.config_parsing import preprocess_config
        from nn4k.utils.config_parsing import get_string_field

        nn_config = preprocess_config(nn_config)
        keys = ("nn_name", "nn_version", "nn_device", "nn_trust_remote_code")
        for key in keys:
            if key in nn_config:
                break
        else:
            return None

        nn_name = get_string_field(nn_config, "nn_name", "NN model name")
        nn_version = get_string_field(nn_config, "nn_version", "NN model version")
        nn_device = nn_config.get("nn_device")
        if nn_device is not None:
            nn_device = get_string_field(nn_config, "nn_device", "NN model device")
        nn_trust_remote_code = nn_config.get("nn_trust_remote_code", False)

        config = LLMExecutorConfig(
            nn_name=nn_name,
            nn_version=nn_version,
            nn_device=nn_device,
            nn_trust_remote_code=nn_trust_remote_code,
        )
        return config
