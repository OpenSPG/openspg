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
from typing import Optional, Union, Tuple, Type

from nn4k.executor import NNExecutor


class NNHub(ABC):
    _hub_instance = None

    @staticmethod
    def get_instance() -> "NNHub":
        """
        Get the NNHub instance. If the instance is not initialized, create a stub `SimpleNNHub`.
        """
        if NNHub._hub_instance is None:
            NNHub._hub_instance = SimpleNNHub()
        return NNHub._hub_instance

    @abstractmethod
    def publish(
        self,
        model_executor: Union[NNExecutor, Tuple[Type[NNExecutor], tuple, dict, tuple]],
        name: str,
        version: str = None,
    ) -> str:
        """
        Publish a model(executor) to hub.

        :param model_executor: An NNExecutor object, which is pickleable.
                               Or a tuple of (class, init_args, kwargs, weight_ids) for creating an NNExecutor,
                               while all these 4 augments are pickleable.

        :param str name: The name of a model, like `llama2`. We do not have a `namespace`.
                         Use a joined name like `alibaba/qwen` to support such features.

        :param str version: Optional. Auto generate a version if this param is not given.

        :return: The published model version.
        :rtype: str
        """
        pass

    @abstractmethod
    def get_model_executor(
        self, name: str, version: str = None
    ) -> Optional[NNExecutor]:
        """
        Get an NNExecutor instance from Hub.

        :param str name: The name of a model.
        :param str version: The version of a model. Get default version of a model if this param is not given.
        :return: The ModelExecutor Instance. None for NotFound.
        :rtype: Optional[NNExecutor]
        """
        pass

    @abstractmethod
    def get_invoker(self, nn_config: dict) -> Optional["NNInvoker"]:
        """
        Get an NNExecutor instance from Hub.

        :param dict nn_config: The config dictionary.
        :return: The NNExecutor Instance. None for NotFound.
        :rtype: Optional[NNInvoker]
        """
        pass

    def start_service(self, name: str, version: str, service_id: str = None, **kwargs):
        raise NotImplementedError("This Hub does not support starting model service.")

    def stop_service(self, name: str, version: str, service_id: str = None, **kwargs):
        raise NotImplementedError("This Hub does not support stopping model service.")

    def get_service(self, name: str, version: str, service_id: str = None):
        raise NotImplementedError("This Hub does not support model services.")


class SimpleNNHub(NNHub):
    def __init__(self) -> None:
        super().__init__()
        self._model_executors = {}

    def _add_executor(
        self,
        executor: Union[NNExecutor, Tuple[Type[NNExecutor], tuple, dict, tuple]],
        name: str,
        version: str = None,
    ):
        from nn4k.consts import NN_VERSION_DEFAULT

        if version is None:
            version = NN_VERSION_DEFAULT
        if self._model_executors.get(name) is None:
            self._model_executors[name] = {version: executor}
        else:
            self._model_executors[name][version] = executor

    def publish(
        self, model_executor: NNExecutor, name: str, version: str = None
    ) -> str:
        from nn4k.consts import NN_VERSION_DEFAULT

        print(
            "WARNING: You are using SimpleNNHub which can only maintain models in memory without data persistence!"
        )
        if version is None:
            version = NN_VERSION_DEFAULT
        self._add_executor(model_executor, name, version)
        return version

    def _create_model_executor(self, cls, init_args, kwargs, weights):
        raise NotImplementedError()

    def get_model_executor(
        self, name: str, version: str = None
    ) -> Optional[NNExecutor]:
        if self._model_executors.get(name) is None:
            return None
        executor = self._model_executors.get(name).get(version)
        if isinstance(executor, NNExecutor):
            return executor
        cls, init_args, kwargs, weights = executor
        executor = self._create_model_executor(cls, init_args, kwargs, weights)
        return executor

    def _add_local_executor(self, nn_config):
        from nn4k.consts import NN_NAME_KEY, NN_NAME_TEXT
        from nn4k.consts import NN_VERSION_KEY, NN_VERSION_TEXT
        from nn4k.executor.hugging_face import HfLLMExecutor
        from nn4k.utils.config_parsing import get_string_field

        executor = HfLLMExecutor.from_config(nn_config)
        nn_name = get_string_field(nn_config, NN_NAME_KEY, NN_NAME_TEXT)
        nn_version = nn_config.get(NN_VERSION_KEY)
        if nn_version is not None:
            nn_version = get_string_field(nn_config, NN_VERSION_KEY, NN_VERSION_TEXT)
        self.publish(executor, nn_name, nn_version)

    def get_invoker(self, nn_config: dict) -> Optional["NNInvoker"]:
        from nn4k.invoker import LLMInvoker
        from nn4k.invoker.openai import OpenAIInvoker
        from nn4k.utils.invoker_checking import is_openai_invoker
        from nn4k.utils.invoker_checking import is_local_invoker

        if is_openai_invoker(nn_config):
            invoker = OpenAIInvoker.from_config(nn_config)
            return invoker

        if is_local_invoker(nn_config):
            invoker = LLMInvoker.from_config(nn_config)
            self._add_local_executor(nn_config)
            return invoker

        return None
