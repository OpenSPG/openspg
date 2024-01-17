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
from typing import List, Union, Type

import networkx as nx

from knext.common.restable import RESTable
from knext.common.runnable import Runnable


class ComponentTypeEnum(str, Enum):
    Builder = "BUILDER"
    Reasoner = "REASONER"
    GraphLearning = "GRAPH_LEARNING"


class Component(Runnable, RESTable, ABC):
    """
    Base class for all component.
    """

    @property
    def id(self):
        return str(id(self))

    @property
    def type(self):
        return

    @property
    def label(self):
        return

    @property
    def name(self):
        return self.__class__.__name__

    def to_dict(self):
        return {"id": self.id, "name": self.name}

    def __hash__(self):
        return id(self)

    def __eq__(self, other):
        return hash(self) == hash(other)

    def __rshift__(
        self,
        other: Union[
            Type["Chain"],
            List[Type["Chain"]],
            Type["Component"],
            List[Type["Component"]],
            None,
        ],
    ):
        from knext.chain.base import Chain
        from knext.component.builder.mapping import SPGTypeMapping
        from knext.component.builder.mapping import _SPGTypeMappings

        if not other:
            return self
        if not isinstance(other, list):
            other = [other]
        dag_list = []
        if all(isinstance(o, SPGTypeMapping) for o in other):
            other = [_SPGTypeMappings(spg_type_mappings=other)]
        for o in other:
            if not o:
                dag = nx.DiGraph()
                self.last = True
                dag.add_node(self)
                dag_list.append(dag)
            if isinstance(o, Component):
                dag = nx.DiGraph()
                dag.add_node(self)
                dag.add_node(o)
                dag.add_edge(self, o)
                dag_list.append(dag)
            elif isinstance(o, Chain):
                dag = nx.DiGraph()
                dag.add_node(self)
                end_nodes = [
                    node
                    for node, out_degree in dag.out_degree()
                    if out_degree == 0 or node.last
                ]
                start_nodes = [
                    node for node, in_degree in o.dag.in_degree() if in_degree == 0
                ]

                if len(end_nodes) > 0 and len(start_nodes) > 0:
                    for end_node in end_nodes:
                        for start_node in start_nodes:
                            combined_dag.add_edge(end_node, start_node)
                combined_dag = nx.compose(dag, o.dag)
                dag_list.append(combined_dag)
        final_dag = nx.compose_all(dag_list)

        return Chain(dag=final_dag)
