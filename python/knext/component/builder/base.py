from abc import ABC
from enum import Enum
from typing import Union

from knext.component.base import Component


class ComponentTypeEnum(str, Enum):
    CsvSourceReader = "CSV_SOURCE"
    LLMBasedExtractor = "LLM_BASED_EXTRACT"
    UserDefinedExtractor = "USER_DEFINED_EXTRACT"
    SPGTypeMapping = "SPG_TYPE_MAPPING"
    RelationMapping = "RELATION_MAPPING"
    SubGraphMapping = "SUBGRAPH_MAPPING"
    KGSinkWriter = "GRAPH_SINK"


class BuilderComponent(Component, ABC):

    @property
    def type(self):
        return ComponentTypeEnum.__members__[self.__class__.__name__].value

    @property
    def input_keys(self):
        return

    @property
    def output_keys(self):
        return


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
