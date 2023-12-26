# -*- coding: utf-8 -*-
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
from typing import Dict, Any, Type

from knext import rest

from knext.operator.invoke_result import InvokeResult


class BaseOp(ABC):
    """Base class for all user-defined operator functions.

    The execution logic of the operator needs to be implemented in the `eval` method.
    """

    """Operator name."""
    name: str
    """Operator description."""
    desc: str = ""
    """Operator params."""
    params: Dict[str, str] = None

    _registry = {}
    _local_path: str
    _version: int
    _has_registered: bool = False

    def __init__(self, params: Dict[str, str] = None):
        self.params = params

    def invoke(self, *args):
        """Used to implement operator execution logic."""
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    def _handle(self, *inputs) -> Dict[str, Any]:
        """Only available for Builder in OpenSPG to call through the pemja tool."""
        pre_input = self._pre_process(*inputs)
        output = self.invoke(*pre_input)
        post_output = self._post_process(output)
        return post_output

    @staticmethod
    def _pre_process(*inputs):
        """Convert data structures in building job into structures in operator before `eval` method."""
        return inputs

    @staticmethod
    def _post_process(output: InvokeResult) -> Dict[str, Any]:
        """Convert result structures in operator into structures in building job after `eval` method."""
        return output.to_dict()

    @classmethod
    def register(cls, name: str, local_path: str):
        """
        Register a class as subclass of BaseOp with name and local_path.
        After registration, the subclass object can be inspected by `BaseOp.by_name(op_name)`.
        """

        def add_subclass_to_registry(subclass: Type["BaseOp"]):
            subclass.name = name
            subclass._local_path = local_path
            if name in cls._registry:
                raise ValueError(
                    f"Operator [{name}] conflict in {subclass._local_path} and {cls.by_name(name)._local_path}."
                )
            cls._registry[name] = subclass
            if hasattr(subclass, "bind_to"):
                subclass.__bases__[0].bind_schemas[subclass.bind_to] = name
            return subclass

        return add_subclass_to_registry

    @classmethod
    def by_name(cls, name: str):
        """Reflection from op name to subclass object of BaseOp."""
        if name in cls._registry:
            subclass = cls._registry[name]
            return subclass
        else:
            raise ValueError(f"{name} is not a registered name for {cls.__name__}. ")

    def to_rest(self):
        if not hasattr(self, "_local_path"):
            import inspect

            self._local_path = inspect.getfile(self.__class__)
        if not hasattr(self, "name"):
            self.name = self.__class__.__name__
        return rest.OperatorConfig(
            file_path=self._local_path,
            module_path=os.path.splitext(os.path.basename(self._local_path))[0],
            class_name=self.name,
            method="_handle",
            params=self.params,
        )

    @property
    def has_registered(self):
        return self._has_registered
