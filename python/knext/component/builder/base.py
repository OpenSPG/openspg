from abc import ABC
from enum import Enum
from typing import Union

from knext.component.base import Component, ComponentTypeEnum


class ComponentLabelEnum(str, Enum):
    SourceReader = "SOURCE_READER"
    Extractor = "EXTRACTOR"
    Mapping = "MAPPING"
    Evaluator = "EVALUATOR"
    SinkWriter = "SINK_WRITER"


class BuilderComponent(Component, ABC):
    @property
    def type(self):
        return ComponentTypeEnum.Builder

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

    @property
    def label(self):
        return ComponentLabelEnum.SourceReader


class SPGExtractor(BuilderComponent, ABC):
    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]

    @property
    def label(self):
        return ComponentLabelEnum.Extractor


class Mapping(BuilderComponent, ABC):
    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SinkWriter]

    @property
    def label(self):
        return ComponentLabelEnum.Mapping


class SinkWriter(BuilderComponent, ABC):
    @property
    def upstream_types(self):
        return Union[Mapping]

    @property
    def downstream_types(self):
        return None

    @property
    def label(self):
        return ComponentLabelEnum.SinkWriter
