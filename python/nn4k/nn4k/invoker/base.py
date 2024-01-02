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
from typing import Union, Optional
from dataclasses import dataclass, field

from nn4k.executor import LLMExecutor
from nn4k.executor import NNExecutor
from nn4k.nnhub import SimpleNNHub


@dataclass
class NNInvokerConfig:
    invoker_type: str = field(
        default="LLM", metadata={"help": "type of the invoker to use; defaul to 'LLM'"}
    )


@dataclass
class LLMInvokerConfig(NNInvokerConfig):
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

    def submit_inference(self, submit_mode="k8s"):
        # TODO. maybe like:
        # engine.submit(self._nn_config, "xx_executor.execute_inference()")
        raise NotImplementedError()

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
        raise NotImplementedError()

    def local_inference(self, data, **kwargs):
        """
        Implement local inference logic in derived invoker classes.
        """
        raise NotImplementedError()

    def warmup_local_model(self):
        """
        Implement local model warming up logic in derived invoker classes.
        """
        pass

    @classmethod
    def try_parse_config(cls, nn_config: Union[str, dict]) -> Optional[NNInvokerConfig]:
        """
        Try parse invoker config from `nn_config`.

        If the derived invoker class accepts config in `nn_config`, it should parse and
        return an instance of `NNInvokerConfig` or its derived class which will be passed
        to `from_config` later, otherwise it should return `None`.

        :param nn_config: config attempting to parse, can be dictionary or path to a JSON file
        :type nn_config: str or dict
        :rtype: NNInvokerConfig or None
        """
        return None

    @classmethod
    def _from_config(cls, nn_config: NNInvokerConfig) -> "NNInvoker":
        """
        Create invoker instance from `nn_config`.

        :param nn_config: config instance returned from `try_parse_config`
        :type nn_config: NNInvokerConfig
        :rtype: NNInvoker
        """
        o = cls.__new__(cls)
        o._nn_config = nn_config
        return o

    _registered_invoker_classes = []

    @classmethod
    def register_invoker_class(cls, invoker_class):
        """
        Register an invoker class for later use in `from_config`.

        :param invoker_class: a derived class of `NNInvoker`
        """
        if not issubclass(invoker_class, cls):
            message = "invalid invoker class: %r" % (invoker_class,)
            raise TypeError(message)
        if invoker_class in cls._registered_invoker_classes:
            message = "invoker class %r has been registered" % (invoker_class,)
            raise RuntimeError(message)
        cls._registered_invoker_classes.append(invoker_class)

    @classmethod
    def from_config(cls, nn_config: Union[str, dict]):
        """
        Try to create an invoker instance from `nn_config`.

        The last registered invoker class whose `try_parse_config` method
        returns a non-None `NNInvokerConfig` will be used.

        :param nn_config: config to use, can be dictionary or path to a JSON file
        :type nn_config: str or dict
        :rtype: NNInvoker
        :raises RuntimeError: if `nn_config` is not recognized by all the
                              registered invoker classes
        """
        from nn4k.utils.config_parsing import preprocess_config

        nn_config = preprocess_config(nn_config)
        for invoker_class in reversed(cls._registered_invoker_classes):
            config = invoker_class.try_parse_config(nn_config)
            if config is not None:
                invoker = invoker_class._from_config(config)
                return invoker
        message = "nn_config is not recognized by all the registered invoker classes"
        raise RuntimeError(message)


class LLMInvoker(NNInvoker):
    def __init__(self, nn_executor: LLMExecutor) -> None:
        super().__init__(nn_executor)

    def submit_sft(self, submit_mode="k8s"):
        pass

    def submit_rl_tuning(self, submit_mode="k8s"):
        pass

    def local_inference(self, data, **kwargs):
        return self._nn_executor.inference(data, **kwargs)

    def warmup_local_model(self):
        name = self._nn_config.nn_name
        version = self._nn_config.nn_version
        executor = self.hub.get_model_executor(name, version)
        if executor is None:
            message = "model %r version %r " % (name, version)
            message += "is not found in the model hub"
            raise RuntimeError(message)
        self._nn_executor: LLMExecutor = executor
        self._nn_executor.load_model()
        self._nn_executor.warmup_inference()

    @classmethod
    def try_parse_config(
        cls, nn_config: Union[str, dict]
    ) -> Optional[LLMInvokerConfig]:
        from nn4k.utils.config_parsing import preprocess_config
        from nn4k.utils.config_parsing import get_string_field

        nn_config = preprocess_config(nn_config)
        invoker_type = nn_config.get("invoker_type")
        if invoker_type != "LLM":
            return None

        nn_name = get_string_field(nn_config, "nn_name", "NN model name")
        nn_version = get_string_field(nn_config, "nn_version", "NN model version")
        nn_device = nn_config.get("nn_device")
        if nn_device is not None:
            nn_device = get_string_field(nn_config, "nn_device", "NN model device")
        nn_trust_remote_code = nn_config.get("nn_trust_remote_code", False)

        config = LLMInvokerConfig(
            nn_name=nn_name,
            nn_version=nn_version,
            nn_device=nn_device,
            nn_trust_remote_code=nn_trust_remote_code,
        )
        return config
