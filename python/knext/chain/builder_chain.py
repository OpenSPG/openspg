from typing import Union, List

from knext import rest
from knext.chain.base import Chain
from knext.component.builder.extractor import SPGExtractor
from knext.component.builder.mapping import Mapping
from knext.component.builder.sink_writer import SinkWriter
from knext.component.builder.source_reader import SourceReader


class BuilderChain(Chain):

    # source_node: SourceReader
    #
    # process_nodes: List[Union[SPGExtractor, Mapping]]
    #
    # sink_node: SinkWriter

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
        from knext.client.builder import BuilderClient
        client = BuilderClient(**kwargs)
        client.execute(self, **kwargs)

    @classmethod
    def from_chain(cls, chain):
        return cls(dag=chain.dag)

    def to_rest(self) -> rest.Pipeline:
        nodes, edges = [], []
        for node in self.dag.nodes:
            nodes.append(node.to_rest())
        for edge in self.dag.edges:
            edges.append(rest.Edge(_from=edge[0].id, to=edge[1].id))
        dag_config = rest.Pipeline(
            nodes=nodes,
            edges=edges
        )
        return dag_config
