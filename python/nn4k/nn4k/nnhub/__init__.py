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
    @abstractmethod
    def publish(
        self,
        model_executor: Union[
            NNExecutor, Tuple[Type[NNExecutor], tuple, tuple, dict, tuple]
        ],
        name: str,
        version: str = None,
    ) -> str:
        """
        Publish a model(executor) to hub.
        Args:
            model_executor: An NNExecutor object, which is pickleable.
                Or a tuple of (class, init_args, inference_args, kwargs, weight_ids) for creating an NNExecutor
                , while all these 5 augments are pickleable.
            name: The name of a model, like `llama2`.
                We do not have a `namespace`. Use a joined name like `alibaba/qwen` to support such features.
            version: Optional. Auto generate a version if this param is not given.
        Returns:
            The published model version.
        """
        pass

    @abstractmethod
    def get_model_executor(
        self, name: str, version: str = None
    ) -> Optional[NNExecutor]:
        """
        Get a ModelExecutor instance from Hub.
        Args:
            name: The name of a model.
            version: The version of a model. Get default version of a model if this param is not given.
        Returns:
            The ModelExecutor Instance. None for NotFound.
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

        # init executor info.
        # TODO
        # self._add_executor((DeepKEExecutor, xxx))
        # self._add_executor((DeepKEExecutor, xxx))
        # self._add_executor((HF, xxx))
        # self._add_executor((HF, xxx))

    def _add_executor(
        self,
        executor: Union[NNExecutor, Tuple[Type[NNExecutor], tuple, tuple, dict, tuple]],
        name: str,
        version: str = None,
    ):
        if version is None:
            version = "default"
        if self._model_executors.get(name) is None:
            self._model_executors[name] = {version: executor}
        else:
            self._model_executors[name][version] = executor

    def publish(
        self, model_executor: NNExecutor, name: str, version: str = None
    ) -> str:
        print(
            "WARNING: You are using SimpleNNHub which can only maintain models in memory without data persistence!"
        )
        if version is None:
            version = "default"
        self._add_executor(model_executor, name, version)
        return version

    def _create_model_executor(self, cls, init_args, inference_args, kwargs, weights):
        raise NotImplementedError()

    def get_model_executor(
        self, name: str, version: str = None
    ) -> Optional[NNExecutor]:
        if self._model_executors.get(name) is None:
            return None
        executor = self._model_executors.get(name).get(version)
        if isinstance(executor, NNExecutor):
            return executor
        cls, init_args, inference_args, kwargs, weights = executor
        executor = self._create_model_executor(
            cls, init_args, inference_args, kwargs, weights
        )
        return executor
