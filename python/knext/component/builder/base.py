from abc import ABC
from typing import Union

from knext.component.base import Component, ComponentTypeEnum, ComponentLabelEnum


class SPGExtractor(Component, ABC):

    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]

    @property
    def type(self):
        return ComponentTypeEnum.Builder

    @property
    def label(self):
        return ComponentLabelEnum.Extractor


class Mapping(Component, ABC):

    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SinkWriter]

    @property
    def type(self):
        return ComponentTypeEnum.Builder

    @property
    def label(self):
        return ComponentLabelEnum.Mapping


class SinkWriter(Component, ABC):

    @property
    def upstream_types(self):
        return Union[Mapping]

    @property
    def downstream_types(self):
        return None

    @property
    def type(self):
        return ComponentTypeEnum.Builder

    @property
    def label(self):
        return ComponentLabelEnum.SinkWriter


class SourceReader(Component, ABC):

    @property
    def upstream_types(self):
        return None

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]

    @property
    def type(self):
        return ComponentTypeEnum.Builder

    @property
    def label(self):
        return ComponentLabelEnum.SourceReader
