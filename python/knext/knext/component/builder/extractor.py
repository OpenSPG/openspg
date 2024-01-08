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

import json
from typing import Dict, List, Sequence

from knext.client.operator import OperatorClient
from knext.common.runnable import Input, Output
from knext.component.builder.base import SPGExtractor
from knext.operator.spg_record import SPGRecord
from knext import rest
from knext.operator.op import PromptOp, ExtractOp
from nn4k.invoker import NNInvoker


class LLMBasedExtractor(SPGExtractor):
    """A Builder Component that extracting structured data from long texts by invoking large language model.

    Examples:
        prompt_op = REPrompt(
                    spg_type_name=Medical.Disease,
                    property_names=[
                        Medical.Disease.complication,
                        Medical.Disease.commonSymptom,
                    ]
                )
        extract = LLMBasedExtractor(
                    llm=NNInvoker.from_config("./config.json"),
                    prompt_ops=[prompt_op]
                )

    """

    """Knowledge extract operator of this component."""
    llm: NNInvoker
    """PromptOps."""
    prompt_ops: List[PromptOp]

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return SPGRecord

    def invoke(self, input: Input) -> Sequence[Output]:
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being invoked separately."
        )

    def submit(self):
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being submitted separately."
        )

    def to_rest(self):
        """Transforms `LLMBasedExtractor` to REST model `ExtractNodeConfig`."""
        params = dict()
        params["model_config"] = json.dumps(self.llm.init_args)
        params["prompt_config"] = json.dumps(
            [OperatorClient().serialize(op.to_rest()) for op in self.prompt_ops]
        )
        from knext.operator.builtin.online_runner import _BuiltInOnlineExtractor

        extract_op = _BuiltInOnlineExtractor(params)
        config = rest.UserDefinedExtractNodeConfig(operator_config=extract_op.to_rest())

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass


class UserDefinedExtractor(SPGExtractor):
    """A Process Component that transforming unstructured data into structured data by extract operator.

    Examples:
        extract = UserDefinedExtractor(
                    extract_op=DemoExtractOp(params={"config": "1"})
                )

    """

    """Knowledge extract operator of this component."""
    extract_op: ExtractOp

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return Dict[str, str]

    def invoke(self, input: Input) -> Sequence[Output]:
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being invoked separately."
        )

    def submit(self):
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being submitted separately."
        )

    def to_rest(self):
        """Transforms `UserDefinedExtractor` to REST model `UserDefinedExtractNodeConfig`."""
        operator_config = self.extract_op.to_rest()
        config = rest.UserDefinedExtractNodeConfig(operator_config=operator_config)

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        return cls()
