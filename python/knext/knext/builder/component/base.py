# -*- coding: utf-8 -*-
# Copyright 2023 OpenSPG Authors
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
from functools import cmp_to_key
from typing import Union

from knext.common.base.component import Component


class ComponentTypeEnum(str, Enum):
    CSVReader = "CSV_SOURCE"
    LLMBasedExtractor = "LLM_BASED_EXTRACT"
    UserDefinedExtractor = "USER_DEFINED_EXTRACT"
    SPGTypeMapping = "SPG_TYPE_MAPPING"
    RelationMapping = "RELATION_MAPPING"
    SubGraphMapping = "SUBGRAPH_MAPPING"
    KGWriter = "GRAPH_SINK"


class BuilderComponent(Component, ABC):
    """
    Abstract base class for all builder component.
    """

    @property
    def type(self):
        return ComponentTypeEnum.__members__[self.__class__.__name__].value


class SourceReader(BuilderComponent, ABC):
    """
    Abstract base class for all source reader component.
    """

    @property
    def upstream_types(self):
        return None

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]


class SPGExtractor(BuilderComponent, ABC):
    """
    Abstract base class for all SPG extractor component.
    """

    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]


class Mapping(BuilderComponent, ABC):
    """
    Abstract base class for all mapping component.
    """

    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SinkWriter]

    @staticmethod
    def sort_by_dependency(mappings: list):

        from knext.builder.component import SPGTypeMapping

        def comparator(x: SPGTypeMapping, y: SPGTypeMapping):
            if x.spg_type_name in y.dependencies:
                return -1
            elif y.spg_type_name in x.dependencies:
                return 1
            else:
                return 0

        from knext.builder import rest

        if len(mappings) == 1:
            return rest.SpgTypeMappingNodeConfigs(
                mapping_node_configs=[m.to_rest().node_config for m in mappings]
            )
        mappings = sorted(mappings, key=cmp_to_key(comparator))
        return rest.SpgTypeMappingNodeConfigs(
            mapping_node_configs=[m.to_rest().node_config for m in mappings]
        )


class SinkWriter(BuilderComponent, ABC):
    """
    Abstract base class for all sink writer component.
    """

    @property
    def upstream_types(self):
        return Union[Mapping]

    @property
    def downstream_types(self):
        return None
