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
from abc import ABC
from typing import Type, List

from knext import rest


class RESTable(ABC):
    @property
    def upstream_types(self) -> List[Type["RESTable"]]:
        return []

    @property
    def downstream_types(self) -> List[Type["RESTable"]]:
        return []

    def to_rest(self) -> rest.Node:
        raise NotImplementedError(
            f"`to_rest` is not currently supported for {self.__class__.__name__}."
        )

    @classmethod
    def from_rest(cls, node: rest.Node):
        raise NotImplementedError(
            f"`from_rest` is not currently supported for {cls.__name__}."
        )

    def submit(self):
        raise NotImplementedError(
            f"`submit` is not currently supported for {self.__class__.__name__}."
        )
