# -*- coding: utf-8 -*-
# Copyright 2023 Ant Group CO., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.
from knext import rest
from knext.chain.base import Chain


class BuilderChain(Chain):
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

        client = BuilderClient()
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
        dag_config = rest.Pipeline(nodes=nodes, edges=edges)
        return dag_config
