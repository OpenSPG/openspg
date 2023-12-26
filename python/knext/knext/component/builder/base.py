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
from typing import Union

from knext.component.base import Component


class ComponentTypeEnum(str, Enum):
    CSVReader = "CSV_SOURCE"
    LLMBasedExtractor = "LLM_BASED_EXTRACT"
    UserDefinedExtractor = "USER_DEFINED_EXTRACT"
    SPGTypeMapping = "SPG_TYPE_MAPPING"
    RelationMapping = "RELATION_MAPPING"
    SubGraphMapping = "SUBGRAPH_MAPPING"
    KGWriter = "GRAPH_SINK"


class BuilderComponent(Component, ABC):
    @property
    def type(self):
        return ComponentTypeEnum.__members__[self.__class__.__name__].value


class SourceReader(BuilderComponent, ABC):
    @property
    def upstream_types(self):
        return None

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]


class SPGExtractor(BuilderComponent, ABC):
    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]


class Mapping(BuilderComponent, ABC):
    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SinkWriter]


class SinkWriter(BuilderComponent, ABC):
    @property
    def upstream_types(self):
        return Union[Mapping]

    @property
    def downstream_types(self):
        return None
