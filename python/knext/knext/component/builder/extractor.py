import json
from typing import Union, Dict, List, Sequence

from knext.client.operator import OperatorClient
from knext.common.runnable import Input, Output
from knext.component.builder.base import SPGExtractor
from knext.operator.spg_record import SPGRecord
from knext import rest
from knext.operator.op import PromptOp, ExtractOp

# try:
from nn4k.invoker.base import NNInvoker  # noqa: F403

# except ImportError:
#     pass


class LLMBasedExtractor(SPGExtractor):
    """A Process Component that transforming unstructured data into structured data.

    Examples:
        extract = UserDefinedExtractor(
                    output_fields=["id", 'riskMark', 'useCert']
                ).set_operator("DemoExtractOp")

    """

    """Knowledge extract operator of this component."""
    llm: NNInvoker
    """PromptOps"""
    prompt_ops: List[PromptOp]

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return Union[Dict[str, str], SPGRecord]

    @property
    def input_keys(self):
        return None

    @property
    def output_keys(self):
        return self.output_fields

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
        params["model_config"] = json.dumps(self.llm._nn_config)
        api_client = OperatorClient()._rest_client.api_client
        params["prompt_config"] = json.dumps(
            [
                api_client.sanitize_for_serialization(op.to_rest())
                for op in self.prompt_ops
            ],
            ensure_ascii=False,
        )
        from knext.operator.builtin.online_runner import _BuiltInOnlineExtractor

        extract_op = _BuiltInOnlineExtractor(params)
        config = rest.UserDefinedExtractNodeConfig(operator_config=extract_op.to_rest())

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass


class UserDefinedExtractor(SPGExtractor):
    """A Process Component that transforming unstructured data into structured data.

    Examples:
        extract_1 = UserDefinedExtractor(
                    extract_op=DemoExtractOp(params={"config": "1"})
                )

        extract_2 = UserDefinedExtractor().set_operator(
                    op_name="DemoExtractOp",
                    params={"config": "2"}
                )

    """

    """Knowledge extract operator of this component."""
    extract_op: ExtractOp = None

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return Union[Dict[str, str], SPGRecord]

    @property
    def input_keys(self):
        return None

    @property
    def output_keys(self):
        return self.output_fields

    def set_operator(self, op_name: str, params: Dict[str, str] = None):
        """Sets knowledge extract operator to this component."""
        self.extract_op = ExtractOp.by_name(op_name)(params)
        return self

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
