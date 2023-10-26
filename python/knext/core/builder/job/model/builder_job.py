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

from abc import ABC
from enum import Enum
from typing import Dict, Type


class OperationTypeEnum(str, Enum):
    Add = "ADD"
    Delete = "DELETE"


class BuilderJob(ABC):
    """Base class for all knowledge builder jobs.
    A builder job consists of multiple components.
    The declaration of components and the dependencies between components need to be implemented in `build` method.
    """

    parallelism: int
    operation_type: OperationTypeEnum
    lead_to: bool

    _registry: Dict[str, Type] = {}
    _local_path: str

    def build(self):
        """All classes as subclass of BuilderJob need to implement this method,
        to declare the DAG structure of the builder job.
        """
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `build` method."
        )

    @classmethod
    def register(cls, name: str, local_path: str):
        """Register a class as subclass of BuilderJob with name and local_path.
        After registration, the subclass object can be inspected by `BuilderJob.by_name(job_name)`.
        """

        def add_subclass_to_registry(subclass: Type["BuilderJob"]):
            subclass.name = name
            subclass._local_path = local_path
            if name in cls._registry:
                raise ValueError(
                    f"BuilderJob [{name}] conflict in {subclass._local_path} and {cls.by_name(name)._local_path}."
                )

            cls._registry[name] = subclass
            return subclass

        return add_subclass_to_registry

    @classmethod
    def by_name(cls, name: str):
        """Reflection from job name to subclass object of BuilderJob."""
        if name in BuilderJob._registry:
            subclass = BuilderJob._registry[name]
            return subclass
        else:
            raise ValueError(f"{name} is not a registered name for {cls.__name__}. ")
