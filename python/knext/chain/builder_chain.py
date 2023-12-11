from typing import Union, List

from knext.chain.base import Chain
from knext.client.builder import BuilderClient
from knext.client.model.builder_job import BuilderJob
from knext.component.builder.extractor import SPGExtractor
from knext.component.builder.mapping import Mapping
from knext.component.builder.sink_writer import SinkWriter
from knext.component.builder.source_reader import SourceReader


class BuilderChain(Chain):

    source_node: SourceReader

    process_nodes: List[Union[SPGExtractor, Mapping]]

    sink_node: SinkWriter

    @property
    def input_types(self):
        return None

    @property
    def output_types(self):
        return None

    @classmethod
    def from_config(cls):
        return cls()

    def invoke(self, **kwargs):
        client = BuilderClient(**kwargs)
        client.execute(self, **kwargs)
