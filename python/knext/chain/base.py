from abc import ABC
from typing import Union, Type, List

import networkx as nx

from knext.common.restable import RESTable
from knext.common.runnable import Runnable


class Chain(Runnable, RESTable):

    dag: nx.DiGraph

    def submit(self):
        pass

    def to_rest(self):
        pass

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
