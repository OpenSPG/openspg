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

from abc import ABC
from enum import Enum
from typing import List, Dict, Any, Type

from knext.operator.eval_result import EvalResult
from knext.operator.spg_record import SPGRecord


class OperatorTypeEnum(str, Enum):
    EntityLinkOp = "ENTITY_LINK"
    EntityFuseOp = "ENTITY_FUSE"
    PropertyNormalizeOp = "PROPERTY_NORMALIZE"
    KnowledgeExtractOp = "KNOWLEDGE_EXTRACT"


class BaseOp(ABC):
    """Base class for all user-defined operator functions.

    The execution logic of the operator needs to be implemented in the `eval` method.
    """

    name: str
    desc: str = ""
    bind_to: Type = None

    _registry = {}
    _local_path: str
    _type: str
    _version: int

    def __init__(self, params: Dict[str, str] = None):
        self.params = params

    def eval(self, *args):
        """Used to implement operator execution logic."""
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `eval` method."
        )

    def handle(self, *inputs) -> Dict[str, Any]:
        """Only available for Builder in OpenKgEngine to call through the pemja tool."""
        pre_input = self._pre_process(*inputs)
        output = self.eval(*pre_input)
        post_output = self._post_process(output)
        return post_output

    @staticmethod
    def _pre_process(*inputs):
        """Convert data structures in building job into structures in operator before `eval` method."""
        pass

    @staticmethod
    def _post_process(output: EvalResult) -> Dict[str, Any]:
        """Convert result structures in operator into structures in building job after `eval` method."""
        pass

    @classmethod
    def register(cls, name: str, local_path: str):
        """
        Register a class as subclass of BaseOp with name and local_path.
        After registration, the subclass object can be inspected by `BaseOp.by_name(op_name)`.
        """

        def add_subclass_to_registry(subclass: Type["BaseOp"]):
            subclass.name = name
            subclass._local_path = local_path
            subclass._type = OperatorTypeEnum[subclass.__base__.__name__]
            if name in cls._registry:
                raise ValueError(
                    f"Operator [{name}] conflict in {subclass._local_path} and {cls.by_name(name)._local_path}."
                )
            cls._registry[name] = subclass
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
