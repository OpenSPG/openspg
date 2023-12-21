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
from typing import List, Dict, Any, Tuple, TypeVar

from knext.common.schema_helper import SPGTypeHelper, PropertyHelper
from knext.operator.base import BaseOp
from knext.operator.invoke_result import InvokeResult
from knext.operator.spg_record import SPGRecord


SPGTypeName = TypeVar('SPGTypeName', str, SPGTypeHelper)
PropertyName = TypeVar('PropertyName', str, PropertyHelper)


class ExtractOp(BaseOp, ABC):
    """Base class for all knowledge extract operators."""

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def invoke(self, record: Dict[str, str]) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return inputs[0],

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, InvokeResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return InvokeResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return InvokeResult[List[SPGRecord]](output).to_dict()


class LinkOp(BaseOp, ABC):
    """Base class for all entity link operators."""

    bind_to: SPGTypeName

    _bind_schemas: Dict[SPGTypeName, str] = {}

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def invoke(self, property: str, subject_record: SPGRecord) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return inputs[0], SPGRecord.from_dict(inputs[1])

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, InvokeResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return InvokeResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return InvokeResult[List[SPGRecord]](output).to_dict()


class FuseOp(BaseOp, ABC):
    """Base class for all entity fuse operators."""

    bind_to: SPGTypeName

    _bind_schemas: Dict[SPGTypeName, str] = {}

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def invoke(self, records: List[SPGRecord]) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return [
            SPGRecord.from_dict(input) for input in inputs[0]
        ]

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, InvokeResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return InvokeResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return InvokeResult[List[SPGRecord]](output).to_dict()


class PromptOp(BaseOp, ABC):
    """Base class for all prompt operators."""

    template: str

    def __init__(self, **kwargs):
        super().__init__()

    def build_prompt(self, variables: Dict[str, str]) -> str:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `build_prompt` method."
        )

    def parse_response(self, response: str) -> List[SPGRecord]:
        pass

    def build_next_variables(self, variables: Dict[str, str], response: str) -> List[Dict[str, str]]:
        pass

    def invoke(self, *args):
        pass


class PredictOp(BaseOp, ABC):

    bind_to: Tuple[SPGTypeName, PropertyName, SPGTypeName]

    _bind_schemas: Dict[Tuple[SPGTypeName, PropertyName, SPGTypeName], str] = {}

    def invoke(self, subject_record: SPGRecord) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return [
            SPGRecord.from_dict(input) for input in inputs[0]
        ]

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, InvokeResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return InvokeResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return InvokeResult[List[SPGRecord]](output).to_dict()
