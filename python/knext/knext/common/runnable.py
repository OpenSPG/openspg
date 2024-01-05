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

from typing import TypeVar, Sequence, Type

from pydantic import BaseConfig, BaseModel

Other = TypeVar("Other")

Input = TypeVar("Input", contravariant=True)
Output = TypeVar("Output", covariant=True)


class Runnable(BaseModel):
    """
    Abstract base class that can be invoked synchronously.
    """

    _last: bool = False

    @property
    def input_types(self) -> Type[Input]:
        """The type of input this Runnable object accepts specified as a type annotation."""
        return

    @property
    def output_types(self) -> Type[Output]:
        """The type of output this Runnable object produces specified as a type annotation."""
        return

    def invoke(self, input: Input) -> Sequence[Output]:
        """Transform an input into an output sequence synchronously."""
        raise NotImplementedError(
            f"`invoke` is not currently supported for {self.__class__.__name__}."
        )

    def __rshift__(self, other: Other):
        raise NotImplementedError("To be implemented in subclass")

    class Config(BaseConfig):
        arbitrary_types_allowed = True
