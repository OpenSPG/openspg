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

from knext import rest
from knext.component.builder.base import SinkWriter
from knext.operator.spg_record import SPGRecord


class KGWriter(SinkWriter):
    """The Sink Component that writing data to KG storage.

    Args:
        None
    Examples:
        sink = KGWriter()

    """

    @property
    def input_types(self):
        return SPGRecord

    @property
    def output_types(self):
        return None

    def to_rest(self):
        """Transforms `KGWriter` to REST model `GraphStoreSinkNodeConfig`."""
        config = rest.GraphStoreSinkNodeConfig()
        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def invoke(self, input):
        pass

    def submit(self):
        pass
