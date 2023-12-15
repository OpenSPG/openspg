from abc import ABC
from typing import List, Dict, Any, Union

from knext.operator.base import BaseOp
from knext.operator.eval_result import EvalResult
from knext.operator.spg_record import SPGRecord


class ExtractOp(BaseOp, ABC):
    """Base class for all knowledge extract operators."""

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def invoke(self, record: Dict[str, str]) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `eval` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return SPGRecord.from_dict(inputs[0]).properties,

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, EvalResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return EvalResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return EvalResult[List[SPGRecord]](output).to_dict()


class LinkOp(BaseOp, ABC):
    """Base class for all entity link operators."""

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def eval(self, property: str, record: SPGRecord) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `eval` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return inputs[0], SPGRecord.from_dict(inputs[1])

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, EvalResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return EvalResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return EvalResult[List[SPGRecord]](output).to_dict()


class FuseOp(BaseOp, ABC):
    """Base class for all entity fuse operators."""

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def eval(
        self, source_record: SPGRecord, target_records: List[SPGRecord]
    ) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `eval` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return SPGRecord.from_dict(inputs[0]), [
            SPGRecord.from_dict(input) for input in inputs[1]
        ]

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, EvalResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return EvalResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return EvalResult[List[SPGRecord]](output).to_dict()


class PromptOp(BaseOp, ABC):
    """Base class for all prompt operators."""

    template: str

    def build_prompt(self, params: Dict[str, str]) -> str:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `build_prompt` method."
        )

    def parse_response(
        self, response: str
    ) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `parse_response` method."
        )

    def build_params(self, record: Dict[str, str], response: str) -> List[Dict[str, str]]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `build_placeholder` method."
        )
