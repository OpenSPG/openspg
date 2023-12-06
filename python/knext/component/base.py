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


from abc import ABC, abstractmethod
from enum import Enum
from typing import List, Union, TypeVar, Generic, Any, Dict, Tuple, Type

from knext import rest


Other = TypeVar("Other")


class ComponentTypeEnum(str, Enum):
    Builder = "BUILDER"


class ComponentLabelEnum(str, Enum):
    SourceReader = "SOURCE_READER"
    Extractor = "EXTRACTOR"
    Mapping = "MAPPING"
    Evaluator = "EVALUATOR"
    SinkWriter = "SINK_WRITER"


class MappingTypeEnum(str, Enum):
    SPGType = "SPG_TYPE"
    Relation = "RELATION"


class SPGTypeHelper:
    pass


class PropertyHelper:
    pass


class RESTable(ABC):

    @property
    def upstream_types(self):
        raise NotImplementedError("To be implemented in subclass")

    @property
    def downstream_types(self):
        raise NotImplementedError("To be implemented in subclass")

    @abstractmethod
    def to_rest(self):
        raise NotImplementedError("To be implemented in subclass")

    @classmethod
    def from_rest(cls, node: rest.Node):
        raise NotImplementedError("To be implemented in subclass")

    @abstractmethod
    def submit(self):
        raise NotImplementedError("To be implemented in subclass")


class Runnable(ABC):

    @property
    def input_types(self) -> Input:
        return

    @property
    def output_types(self) -> Output:
        return

    @abstractmethod
    def invoke(self, input: Input) -> Output:
        raise NotImplementedError("To be implemented in subclass")

    def __rshift__(
            self,
            other: Type['Runnable']
    ) -> Type['Runnable']:
        """Compose this runnable with another object to create a RunnableSequence."""
        return Chain(first=self, last=coerce_to_runnable(other))


class Component(ABC):
    """
    Base class for all component.
    """

    def id(self):
        return str(id(self))

    @property
    def type(self):
        return

    @property
    def label(self):
        return

    @property
    def name(self):
        return

    def to_dict(self):
        return self.__dict__

