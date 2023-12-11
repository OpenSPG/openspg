from typing import Union, Type, List, Sequence, TypeVar, Generic

import networkx as nx

from knext import rest
from knext.common.restable import RESTable
from knext.common.runnable import Runnable, Input, Output


class Chain(Runnable, RESTable):

    first: RESTable

    last: RESTable

    dag: nx.DiGraph

    def invoke(self, input=None, **kwargs):
        pass

    @property
    def upstream_types(self) -> Type['RESTable']:
        return self.first.upstream_types

    @property
    def downstream_types(self) -> Type['RESTable']:
        return self.last.downstream_types

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def submit(self):
        raise ValueError("Not support yet.")

    def to_rest(self) -> rest.Pipeline:
        nodes, edges = [], []
        for node in self.g.nodes:
            nodes.append(node.to_rest())
        for edge in self.g.edges:
            edges.append(rest.Edge(_from=edge[0].id, to=edge[1].id))

        dag_config = rest.Pipeline(nodes=nodes, edges=edges)
        return dag_config

    def __rshift__(self, other: Union[
        Type['Chain'],
        List[Type['Chain']],
        Type['Component'],
        List[Type['Component']],
        None
    ]):
        from knext.component.base import Component
        if not other:
            return self
        if not isinstance(other, list):
            other = [other]
        dag_list = []
        for o in other:
            if not o:
                dag_list.append(o.dag)
            if isinstance(o, Component):
                end_nodes = [node for node, out_degree in self.dag.out_degree() if out_degree == 0 or node.last]
                dag = nx.DiGraph(self.dag)
                if len(end_nodes) > 0:
                    for end_node in end_nodes:
                        dag.add_edge(end_node, o)
                dag.add_node(o)
                dag_list.append(dag)
            elif isinstance(o, Chain):
                combined_dag = nx.compose(self.dag, o.dag)
                end_nodes = [node for node, out_degree in self.dag.out_degree() if out_degree == 0 or node.last]
                start_nodes = [node for node, in_degree in o.dag.in_degree() if in_degree == 0]

                if len(end_nodes) > 0 and len(start_nodes) > 0:
                    for end_node in end_nodes:
                        for start_node in start_nodes:
                            combined_dag.add_edge(end_node, start_node)
        final_dag = nx.compose_all(dag_list)
        return Chain(dag=final_dag)
