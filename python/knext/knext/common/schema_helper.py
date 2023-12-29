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


class SchemaHelper(ABC, str):

    __type_name__: str

    def __init__(self, type_name: str):
        self.__type_name__ = type_name


class SPGTypeHelper(SchemaHelper):
    def __init__(self, type_name: str):
        super().__init__(type_name)


class PropertyHelper(SchemaHelper):
    def __init__(self, type_name: str):
        super().__init__(type_name)
