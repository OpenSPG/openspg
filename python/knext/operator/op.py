import json
from abc import ABC
from typing import List, Dict, Any, Union

from knext.operator.base import BaseOp
from knext.operator.eval_result import EvalResult
from knext.operator.spg_record import SPGRecord


class ExtractOp(BaseOp, ABC):
    """Base class for all knowledge extract operators."""

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def eval(self, record: Dict[str, str]) -> List[SPGRecord]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `eval` method."
        )

    @staticmethod
    def _pre_process(*inputs):
        return (SPGRecord.from_dict(inputs[0]).properties,)

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
        self, source_SPGRecord: SPGRecord, target_SPGRecordes: List[SPGRecord]
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


class PromptOp(ExtractOp, ABC):
    """Base class for all prompt operators."""

    template: str

    def __init__(self, params: Dict[str, str] = None):
        super().__init__(params)

    def build_prompt(self, record: Dict[str, str]) -> str:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `build_prompt` method."
        )

    def parse_response(self, response: str) -> Union[List[Dict[str, str]], List[SPGRecord]]:
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `parse_response` method."
        )

    def eval(self, *args):
        """Used to implement operator execution logic."""
        raise NotImplementedError(
            f"{self.__class__.__name__} need to implement `eval` method."
        )

    def handle(self, *inputs) -> Dict[str, Any]:
        """Only available for Builder in OpenKgEngine to call through the pemja tool."""
        pre_input = self._pre_process(*inputs)
        output = self.eval(*pre_input)
        post_output = self._post_process(output)
        return post_output

    @staticmethod
    def _pre_process(*inputs):
        """Convert data structures in building job into structures in operator before `eval` method."""
        pass

    @staticmethod
    def _post_process(output) -> Dict[str, Any]:
        if isinstance(output, EvalResult):
            return output.to_dict()
        if isinstance(output, tuple):
            return EvalResult[List[SPGRecord]](*output[:3]).to_dict()
        else:
            return EvalResult[List[SPGRecord]](output).to_dict()

    def parse_response_re(self, response: str) -> List[SPGRecord]:
        """
        识别关系抽取结果，并进行NER，再转换为加工链路协议格式
        """
        result = []
        subject = {}
        re_obj = json.loads(response)
        for spo_item in re_obj:
            # 过滤掉Schema定义以外的谓词
            if spo_item["predicate"] not in self.predicate_zh_to_en_name:
                continue

            subject_properties = {}
            if spo_item["subject"] not in subject:
                subject[spo_item["subject"]] = subject_properties
            else:
                subject_properties = subject[spo_item["subject"]]

            # 获取属性类型
            spo_en_name = self.predicate_zh_to_en_name[spo_item["predicate"]]
            spo_type = self.predicate_type_zh_to_en_name[spo_item["predicate"]]

            if spo_en_name in subject_properties and len(
                subject_properties[spo_en_name]
            ):
                subject_properties[spo_en_name] = (
                    subject_properties[spo_en_name] + "," + spo_item["object"]
                )
            else:
                subject_properties[spo_en_name] = spo_item["object"]

        for k, val in subject.items():
            subject_entity = Vertex(k, "Medical.Disease", val)
            result.append(subject_entity)
        return result
