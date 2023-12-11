from typing import Union

from knext.chain.base import Chain
from knext.component.base import RESTable
from knext.component.builder.extractor import SPGExtractor
from knext.component.builder.mapping import Mapping
from knext.component.builder.sink_writer import SinkWriter
from knext.component.builder.source_reader import SourceReader


class BuilderChain(RESTable, Chain):

    source: SourceReader

    process: Union[
        SPGExtractor,
        Mapping,
    ]

    sink: SinkWriter

    @property
    def input_types(self):
        return None

    @property
    def output_types(self):
        return None

    @classmethod
    def from_config(cls):
        return cls()
