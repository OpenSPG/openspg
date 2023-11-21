# -*- coding: utf-8 -*-
#
#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

import pprint
from typing import TypeVar, Generic, List, Dict, Any

T = TypeVar("T")


class EvalResult(Generic[T]):
    """Data structure in operator, used to represent the results of operator processing.
    Args:
        data: The data after operator process.
        traces: The user defined logs in operator.
        errors: The exceptions in operator.
    """

    def __init__(self, data: T, traces: List[str] = None, errors: List[str] = None):
        self.data = data
        self.traces = traces
        self.errors = errors

    def to_str(self):
        """Returns the string representation of the model"""
        return pprint.pformat(self.to_dict())

    def to_dict(self):
        """Returns the model properties as a dict"""
        return {
            "data": self.data
            if isinstance(self.data, str)
            else [data.to_dict() for data in self.data]
            if self.data
            else [],
            "traces": self.traces,
            "errors": self.errors,
        }

    @staticmethod
    def from_dict(input: Dict[str, Any]):
        """Returns the model from a dict"""
        return EvalResult(input.get("data"), input.get("traces"), input.get("errors"))

    def __repr__(self):
        """For `print` and `pprint`"""
        return self.to_str()
