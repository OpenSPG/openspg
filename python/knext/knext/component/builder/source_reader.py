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

from typing import List, Dict

from pydantic import Field

from knext import rest
from knext.common.runnable import Input, Output
from knext.component.builder.base import SourceReader


class CSVReader(SourceReader):
    """A source component that reading data from CSV file.

    Args:
        local_path: The local path of CSV file.
        columns: The column names that need to be read from the CSV file.
        start_row: The starting number of rows read from the CSV file.
                    If the CSV file includes a header, it needs to be greater than or equal to 2.
    Examples:
        source = CSVReader(
                    local_path="./builder/job/data/App.csv",
                    columns=["id", 'riskMark', 'useCert'],
                    start_row=2
                )
    """

    """The local path of CSV file."""
    local_path: str
    """The column names that need to be read from the CSV file."""
    columns: List[str]
    """The starting number of rows read from the CSV file.
    If the CSV file includes a header, it needs to be greater than or equal to 2."""
    start_row: int = Field(ge=1)

    @property
    def input_types(self) -> Input:
        return None

    @property
    def output_types(self) -> Output:
        return Dict[str, str]

    def invoke(self, input: Input):
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being invoked separately."
        )

    def submit(self):
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being submitted separately."
        )

    def to_rest(self):
        """Transforms `CSVReader` to REST model `CsvSourceNodeConfig`."""
        from pathlib import Path

        absolute_path = str(Path(self.local_path).resolve())
        config = rest.CsvSourceNodeConfig(
            start_row=self.start_row, url=absolute_path, columns=self.columns
        )
        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        return cls(
            local_path=node.node_config.url,
            columns=node.node_config.columns,
            start_row=node.node_config.start_row,
        )
