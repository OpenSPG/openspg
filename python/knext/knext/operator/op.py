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
from typing import List, Dict, Any

import knext.common.cache
from knext.common.schema_helper import SPGTypeName, TripletName
from knext.operator.base import BaseOp
from knext.operator.invoke_result import InvokeResult
from knext.operator.spg_record import SPGRecord

cache = knext.common.cache.LinkCache(500, 60)


class ExtractOp(BaseOp, ABC):
    """Base class for all knowledge extract operators."""

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def invoke(self, record: Dict[str, str]) -> List[Dict[str, str]]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return (inputs[0],)

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

    bind_schemas: Dict[SPGTypeName, str] = {}

    def __init__(self):
        super().__init__()

    def invoke(self, property: str, subject_record: SPGRecord) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    def _handle(self, *inputs) -> Dict[str, Any]:
        _property, subject_record = self._pre_process(*inputs)
        cache_key = str(self.bind_to) + _property
        cache_property = cache.get(cache_key)
        if cache_property:
            output = [SPGRecord(spg_type_name=self.bind_to).upsert_property("id", cache_property)]
        else:
            output = self.invoke(_property, subject_record)
        post_output = self._post_process(output)
        return post_output

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
    """
    Base class for all entity fuse operators.
    """

    """"""
    bind_to: SPGTypeName

    bind_schemas: Dict[SPGTypeName, str] = {}

    def __init__(self):
        super().__init__()

    def link(self, subject_record: SPGRecord) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `link` method."
        )

    def merge(
        self, subject_record: SPGRecord, linked_records: List[SPGRecord]
    ) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `merge` method."
        )

    def invoke(self, subject_records: List[SPGRecord]) -> List[SPGRecord]:
        records = []
        for record in subject_records:
            cache_key = str(self.bind_to) + record.get_property("id", "")
            linked_records = self.link(record)
            merged_records = self.merge(record, linked_records)
            merged_records = list(filter(None.__ne__, merged_records))
            if merged_records:
                cache.put(cache_key, ','.join([_r.get_property("id", "") for _r in merged_records]))
            records.extend(merged_records)
        return records

    @staticmethod
    def _pre_process(*inputs):
        return ([SPGRecord.from_dict(input) for input in inputs[0]],)

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
        return []

    def build_next_variables(
        self, variables: Dict[str, str], response: str
    ) -> List[Dict[str, str]]:
        if isinstance(response, list) and len(response) > 0:
            response = response[0]
        variables.update({f"{self.__class__.__name__}": response})
        return [variables]

    def invoke(self, *args):
        pass


class PredictOp(BaseOp, ABC):
    """Base class for all predict operators."""

    bind_to: TripletName

    bind_schemas: Dict[TripletName, str] = {}

    def __init__(self):
        super().__init__()

    def invoke(self, subject_record: SPGRecord) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `invoke` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return (SPGRecord.from_dict(inputs[0]),)

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, InvokeResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return InvokeResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return InvokeResult[List[SPGRecord]](output).to_dict()
